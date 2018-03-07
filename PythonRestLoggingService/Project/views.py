from flask import render_template, request
from Project import app

@app.after_request
def add_header(r):
    """
    Add headers to both force latest IE rendering engine or Chrome Frame,
    and also to cache the rendered page for 10 minutes.
    """
    r.headers["Cache-Control"] = "no-cache, no-store, must-revalidate"
    r.headers["Pragma"] = "no-cache"
    r.headers["Expires"] = "0"
    r.headers['Cache-Control'] = 'public, max-age=0'
    return r
    
@app.route('/')
def index():
    return render_template(
      'index.html'
   )
      
@app.route('/log/<text>', methods = ['GET', 'POST'])
def log(text):
    with open("log.txt", "a") as myfile:
      myfile.write(text + "\n")
    if request.method == 'POST':
      data = request.data
      with open("log.txt", "a") as myfile:
         myfile.write(data + "\n")
    return text
    
@app.errorhandler(400)
@app.errorhandler(500)
def server_error(e):
    return "Error: " + str(e)