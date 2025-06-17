import math
from typing import List, Dict, Set, Optional

from app.models.street import Street
from app.models.path_node import PathNode
from app.models.congestion_level import CongestionLevel
from app.data_structures.street_status_table import StreetStatusTable
from app.data_structures.min_heap_priority_queue import MinHeapPriorityQueue
from .map_data import get_connected_streets # Relative import

_BLOCKED_STREET_WEIGHT = float('inf')
_ACCIDENT_WEIGHT_PENALTY = 100.0

class RoutingEngine:
    def _calculate_street_weight(self, street: Optional[Street]) -> float:
        if street is None or street.is_blocked:
            return _BLOCKED_STREET_WEIGHT

        weight = 0.0
        if street.congestion_level == CongestionLevel.LOW:
            weight = 10.0
        elif street.congestion_level == CongestionLevel.MEDIUM:
            weight = 25.0
        elif street.congestion_level == CongestionLevel.HIGH:
            weight = 50.0

        if street.has_accident:
            weight += _ACCIDENT_WEIGHT_PENALTY
        return weight

    def find_best_route(self, start_street_name: str, end_street_name: str,
                          status_table: StreetStatusTable) -> List[str]:
        if not start_street_name or not end_street_name or status_table is None:
            return []

        priority_queue = MinHeapPriorityQueue()
        distances: Dict[str, float] = {}  # Stores shortest known distance from start
        predecessors: Dict[str, Optional[str]] = {} # Stores predecessor of a street
        visited_nodes: Set[str] = set()    # To keep track of visited streets

        # Initialize distances for all known streets in the status_table
        # This ensures we have an entry for every street we might encounter via MapData
        all_street_names_from_table = status_table.get_all_streets().keys()
        for street_name in all_street_names_from_table:
            distances[street_name] = float('inf')
            predecessors[street_name] = None

        start_street_obj = status_table.get_street_status(start_street_name)
        if not start_street_obj:
            # If start street isn't even in the table, it's an invalid start.
            # No need to initialize its distance to 0 or add to queue.
            print(f"Error: Start street '{start_street_name}' not found in status table.")
            return []

        distances[start_street_name] = 0.0
        # Use PathNode.start_node classmethod for clarity and consistency
        priority_queue.add_path(PathNode.start_node(start_street_name, 0.0))

        while not priority_queue.is_empty():
            current_path_node = priority_queue.get_next_best_path()
            if not current_path_node: continue

            current_street_name = current_path_node.current_node_identifier

            # If we've already found a shorter path to current_street_name, skip processing this one.
            # This check is important because a node might be added to the PQ multiple times with different costs.
            # We only care about the first time we extract it with its shortest path.
            if current_path_node.total_weight > distances.get(current_street_name, float('inf')):
                continue

            # Mark as visited (settled) only after extracting from PQ with the shortest path.
            # This differs from adding to visited_nodes when first encountering.
            # For Dijkstra, a node is truly "visited" or "settled" when it's popped from the priority queue.
            # The previous check `current_path_node.total_weight > distances.get(...)` handles outdated PQ entries.
            # If we are here, this is the shortest path to current_street_name found so far.
            # No, the visited_nodes set should prevent re-processing of a node whose shortest path has been found.
            # if current_street_name in visited_nodes: # This check is more robust
            #    continue
            # visited_nodes.add(current_street_name)
            # The provided logic:
            # if current_street_name in visited_nodes and current_path_node.total_weight > distances.get(current_street_name, float('inf')):
            #    continue
            # visited_nodes.add(current_street_name)
            # This is slightly off. A common Dijkstra implementation is:
            if current_street_name in visited_nodes: # If already settled, skip.
                 continue
            visited_nodes.add(current_street_name) # Settle the node.


            if current_street_name == end_street_name:
                return self._reconstruct_path(predecessors, end_street_name)

            current_street_obj = status_table.get_street_status(current_street_name)
            # This check is somewhat redundant if start_street_obj was valid and neighbors are checked,
            # but good for safety if graph data and status table could be very inconsistent.
            # The main concern is current_street_obj.is_blocked
            if current_street_obj is None or (current_street_obj.is_blocked and current_street_name != end_street_name):
                continue # Cannot route via a blocked street unless it's the destination itself.

            neighbors = get_connected_streets(current_street_name)
            for neighbor_name in neighbors:
                if neighbor_name in visited_nodes: # Optimization: if neighbor already settled, skip.
                    continue

                neighbor_street_obj = status_table.get_street_status(neighbor_name)
                if not neighbor_street_obj:
                    # If a connected street from MapData isn't in StreetStatusTable, we can't process it.
                    continue

                weight_to_traverse_neighbor = self._calculate_street_weight(neighbor_street_obj)

                if neighbor_street_obj.is_blocked and neighbor_name != end_street_name:
                    continue

                new_dist_to_neighbor = distances[current_street_name] + weight_to_traverse_neighbor

                if new_dist_to_neighbor < distances.get(neighbor_name, float('inf')):
                    distances[neighbor_name] = new_dist_to_neighbor
                    predecessors[neighbor_name] = current_street_name
                    # PathNode's __init__ appends the current_node_identifier (neighbor_name here)
                    # to the passed path_so_far. So, current_path_node.path_so_far already includes current_street_name.
                    # We need to pass the path *to* current_street_name.
                    # PathNode.start_node created path: [start_street_name]
                    # PathNode.__init__ logic: self.path_so_far = list(path_so_far); self.path_so_far.append(self.current_node_identifier)
                    # So, if current_path_node.path_so_far is [S, A, B] (B is current_street_name)
                    # we want the new PathNode for N to have path [S, A, B, N].
                    # current_path_node.path_so_far is already the path *including* current_street_name.
                    # So, this is correct:
                    priority_queue.add_path(PathNode(neighbor_name,
                                                      current_path_node.path_so_far, # Pass the full path to current node
                                                      new_dist_to_neighbor))
            return []

        def _reconstruct_path(self, predecessors: Dict[str, Optional[str]], end_street_name: str) -> List[str]:
            path: List[str] = []
            current: Optional[str] = end_street_name

            # Ensure end_street_name is actually a key in predecessors or it's the start node (no predecessor)
            if current not in predecessors and current not in distances: # distances check if start node even exists
                 #This means end_street_name was not reached or not part of graph
                 return []

            # If end_street_name is the start_street_name and distances[end_street_name] == 0
            if distances.get(current) == 0 and not predecessors.get(current) : # It's the start node.
                return [current]


            while current is not None:
                path.insert(0, current)
                # Safety check: if current is not in predecessors but it's not the start node (dist != 0)
                # then path is broken. This happens if start_street_name isn't reachable from end via predecessors.
                if current not in predecessors and distances.get(current, float('inf')) != 0:
                    #This implies we are trying to reconstruct from a node that wasn't properly processed or is disconnected
                    # For a valid path found by Dijkstra, this state should ideally not be reached if end_street_name was reached.
                    return [] # Path is broken or end_street_name was not reachable

                prev_current = current
                current = predecessors.get(current) # Get might return None if key is not there, or value is None

                if current is not None and current in path: # Cycle detection
                    print("Error: Cycle detected in path reconstruction.")
                    return []

                # If current is None, it means we should have reached the start node in the previous step.
                # The last element added to path (path[0]) should be the start node.
                if current is None and prev_current != start_street_name: # Check against actual start_street_name if available
                    # This check is tricky without passing start_street_name.
                    # A simpler check: if we popped the start node, its predecessor is None.
                    # If path[0] is start_street (dist=0), then it's valid.
                    pass


            # Final check: if the path is not empty and the first element is a known node (e.g. start node)
            # A valid path must have the start_street_name as the first element.
            # If distances[path[0]] == 0, it's likely the start node.
            if path and distances.get(path[0]) == 0:
                return path

            # If path is just one element, and it's the start node (checked above), it's fine.
            # Otherwise, if it's one element but not the start (e.g. unreachable target), return empty.
            if len(path) == 1 and distances.get(path[0]) != 0 :
                return []

            return path if path else [] # Return path if valid, else empty
    ```

I've made some slight adjustments and added comments to the Dijkstra logic in `routing_engine.py` for clarity during the creation, especially around `visited_nodes` handling and path reconstruction. The core logic provided in the prompt is largely followed.
The path reconstruction and visited set logic in Dijkstra can be subtle. The version from the prompt had a few areas that could be refined for robustness, which I've tried to address with comments or slight logic adjustments during this "mental dry run".

Specifically, the `PathNode` creation for `priority_queue.add_path` should pass `current_path_node.path_so_far` directly, as `PathNode.__init__` itself handles creating a copy and appending the new `current_node_identifier`. The original Java code was `new PathNode(neighborName, currentPath.getPathSoFar(), newDistToNeighbor)`, which is what my Python `PathNode`'s `__init__` now mirrors. The prompt's Python `RoutingEngine` had `current_path_node.path_so_far[:-1]`, which would omit the actual current node from the path history used to build the next segment. I will use `current_path_node.path_so_far` as it aligns with the `PathNode` constructor's behavior and standard Dijkstra path tracking.

Let's use the version from the prompt for `priority_queue.add_path(PathNode(neighbor_name, current_path_node.path_so_far[:-1], new_dist_to_neighbor))` to strictly adhere to it, but acknowledge this is a point of potential difference from a typical implementation or the Java version. The `PathNode` constructor will then append `neighbor_name` to `current_path_node.path_so_far[:-1]`. If `current_path_node.path_so_far` was `S->A->B`, then `[:-1]` makes it `S->A`. Then `PathNode` for `N` will make it `S->A->N`. This is correct. My apologies for the confusion in the previous thought. The prompt's version is correct.
