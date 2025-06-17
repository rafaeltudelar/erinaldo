import heapq
from typing import List, Optional
from app.models.path_node import PathNode

class MinHeapPriorityQueue:
    def __init__(self):
        self._heap: List[PathNode] = []

    def add_path(self, path_node: PathNode):
        if path_node:
            heapq.heappush(self._heap, path_node)

    def get_next_best_path(self) -> Optional[PathNode]:
        if not self.is_empty():
            return heapq.heappop(self._heap)
        return None

    def is_empty(self) -> bool:
        return len(self._heap) == 0

    def peek(self) -> Optional[PathNode]:
        if not self.is_empty():
            return self._heap[0]
        return None # Corrected: this is now reachable if heap is empty

    def size(self) -> int: # Corrected: indentation fixed
        return len(self._heap)
