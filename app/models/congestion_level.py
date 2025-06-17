from enum import Enum, auto

class CongestionLevel(Enum):
    LOW = auto()
    MEDIUM = auto()
    HIGH = auto()

    def __str__(self):
        return self.name
