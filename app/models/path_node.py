from typing import List, Any

class PathNode:
    def __init__(self, current_node_identifier: Any, path_so_far: List[Any], total_weight: float):
        self.current_node_identifier = current_node_identifier
        # Ensure path_so_far is a new list and add current_node_identifier
        self.path_so_far = list(path_so_far)
        self.path_so_far.append(self.current_node_identifier)
        self.total_weight = total_weight

    # Constructor for the starting node
    @classmethod
    def start_node(cls, start_node_identifier: Any, initial_weight: float = 0.0):
        # In this version, the __init__ handles appending the start_node_identifier to an empty path list
        return cls(start_node_identifier, [], initial_weight)


    def __lt__(self, other: 'PathNode') -> bool:
        # For min-heap behavior with heapq
        return self.total_weight < other.total_weight

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, PathNode):
            return NotImplemented
        return self.total_weight == other.total_weight and self.current_node_identifier == other.current_node_identifier

    def __str__(self):
        path_str = " -> ".join(map(str, self.path_so_far))
        return f"PathNode(to={self.current_node_identifier}, weight={self.total_weight}, path=[{path_str}])"

    def __repr__(self):
        return self.__str__()
