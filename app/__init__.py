from flask import Flask, request, jsonify
from app.services.routing_engine import RoutingEngine
from app.services.traffic_data_collector import simulate_data_and_update_table
from app.data_structures.street_status_table import StreetStatusTable
import os

# Create a Flask application instance
# The 'template_folder' and 'static_folder' are set to serve index.html and style.css
# from the 'static' directory at the app root.
app = Flask(__name__, static_folder='static', static_url_path='')


@app.route('/')
def index():
    # This will serve static/index.html when accessing the root URL
    return app.send_static_file('index.html')

@app.route('/api/route')
def find_route_api():
    origin = request.args.get('origin')
    destination = request.args.get('destination')

    if not origin or not destination:
        # It's good practice to return a proper HTTP error status
        return jsonify({"error": "Origem e destino são obrigatórios."}), 400

    print(f"API: Received request for route from: {origin} to: {destination}")

    # For simplicity, instantiate services here.
    # In a larger app, you might manage these differently (e.g., singletons, dependency injection).
    status_table = StreetStatusTable()
    routing_engine = RoutingEngine()

    # Simulate fresh data for each request
    simulate_data_and_update_table(status_table)
    # simulate_data_and_update_table prints to console, which is fine for server logs here.

    route = routing_engine.find_best_route(origin, destination, status_table)

    if not route:
        print(f"API: No route found from {origin} to {destination}")
        # Return an empty list or a specific message for the frontend
        return jsonify([]) # Or jsonify({"message": "Nenhuma rota encontrada."})
    else:
        print(f"API: Route found: {' -> '.join(route)}")

    return jsonify(route) # Flask automatically converts list to JSON array


# Optional: Add a simple test route
@app.route('/hello')
def hello():
    return "Hello, Rota Facilitada (Flask)!"
