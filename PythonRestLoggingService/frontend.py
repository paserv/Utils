from os import environ
from Project import app

def runserver():
    HOST = environ.get('SERVER_HOST', '0.0.0.0')
    try:
        PORT = int(environ.get('SERVER_PORT', '5555'))
    except ValueError:
        PORT = 5555
    app.run(HOST, PORT, debug=False, use_reloader=False)

if __name__ == '__main__':
    runserver()
