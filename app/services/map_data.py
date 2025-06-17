from typing import Dict, List

# Simple representation of street connections (graph adjacency list)
# Key: Street name
# Value: List of directly connected street names
_street_connections: Dict[str, List[str]] = {
    "Rua A": ["Rua B", "Rua D"],
    "Rua B": ["Rua A", "Rua C", "Rua E"],
    "Rua C": ["Rua B"],
    "Rua D": ["Rua A"],
    "Rua E": ["Rua B", "Rua F"],
    "Rua F": ["Rua E"]
}

def get_connected_streets(street_name: str) -> List[str]:
    return _street_connections.get(street_name, [])
