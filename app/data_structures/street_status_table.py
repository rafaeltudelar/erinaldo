from typing import Dict, Optional
from app.models.street import Street
from app.models.congestion_level import CongestionLevel

class StreetStatusTable:
    def __init__(self):
        self._street_map: Dict[str, Street] = {}

    def add_street(self, street: Street):
        if street and street.street_name:
            self._street_map[street.street_name] = street

    def get_street_status(self, street_name: str) -> Optional[Street]:
        return self._street_map.get(street_name)

    def update_street_status(self, street_name: str, level: CongestionLevel, has_accident: bool, is_blocked: bool) -> bool:
        street = self.get_street_status(street_name)
        if street:
            street.congestion_level = level
            street.has_accident = has_accident
            street.is_blocked = is_blocked
            return True
        return False # Street not found

    def get_all_streets(self) -> Dict[str, Street]:
        return self._street_map.copy() # Return a copy
