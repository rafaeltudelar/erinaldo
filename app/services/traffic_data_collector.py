from app.data_structures.street_status_table import StreetStatusTable
from app.models.street import Street
from app.models.congestion_level import CongestionLevel

def simulate_data_and_update_table(table: StreetStatusTable):
    if table is None:
        print("Error: StreetStatusTable cannot be None.")
        return

    # Create and add some initial streets
    rua_a = Street("Rua A", CongestionLevel.LOW, False, False)
    rua_b = Street("Rua B", CongestionLevel.MEDIUM, False, False)
    rua_c = Street("Rua C", CongestionLevel.HIGH, True, False)  # Accident on Rua C
    rua_d = Street("Rua D", CongestionLevel.LOW, False, True)   # Rua D is blocked
    rua_e = Street("Rua E", CongestionLevel.MEDIUM, False, False)
    rua_f = Street("Rua F", CongestionLevel.LOW, False, False)

    table.add_street(rua_a)
    table.add_street(rua_b)
    table.add_street(rua_c)
    table.add_street(rua_d)
    table.add_street(rua_e)
    table.add_street(rua_f)

    print("Initial simulated data added to StreetStatusTable:")
    for name, street_obj in table.get_all_streets().items():
        print(f" - {street_obj}")

    print("\nSimulating an update for Rua B...")
    table.update_street_status("Rua B", CongestionLevel.HIGH, True, False)
    print(f"Rua B updated: {table.get_street_status('Rua B')}")

    print("\nSimulating an update for a non-existing street (Rua X)...")
    updated = table.update_street_status("Rua X", CongestionLevel.LOW, False, False)
    print(f"Update status for Rua X: {'Success' if updated else 'Failed (Not Found)'}")
