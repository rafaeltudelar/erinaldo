from .congestion_level import CongestionLevel

class Street:
    def __init__(self, street_name: str, congestion_level: CongestionLevel, has_accident: bool, is_blocked: bool):
        self.street_name = street_name
        self.congestion_level = congestion_level
        self.has_accident = has_accident
        self.is_blocked = is_blocked

    def __str__(self):
        return (f"Street(name='{self.street_name}', congestion={self.congestion_level}, "
                f"accident={self.has_accident}, blocked={self.is_blocked})")

    def __repr__(self):
        return self.__str__()
