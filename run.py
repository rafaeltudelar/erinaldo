from app import app # app is the Flask instance from app/__init__.py

if __name__ == '__main__':
    # Setting host='0.0.0.0' makes it accessible externally if needed,
    # otherwise '127.0.0.1' (default) is fine for local development.
    # debug=True is useful for development as it enables auto-reloading on code changes
    # and provides a debugger.
    app.run(host='0.0.0.0', port=5000, debug=True)
