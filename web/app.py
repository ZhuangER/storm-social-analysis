from flask import Flask, render_template, Response


app = Flask(__name__)

@app.route('/')
@app.route('/map')
def show_map():
  #Basic d3 view = basic.html and app.js
    return render_template("us_map.html")

@app.route('/ca_map')
def ca_map():
	return render_template("ca_map.html")

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)