import numpy as np
from flask import Flask, request, jsonify, render_template
import pickle
from model import processImg


flask_app = Flask(__name__)
model = pickle.load(open("../models/model.pkl", "rb"))

@flask_app.route("/")
def Home():
    return render_template("index.html")

# Process images
@flask_app.route("/process", methods=["POST"])
def processReq():
    data = request.files["img"]
    data.save("img.jpg")
    resp = processImg("img.jpg")
    return resp

@flask_app.route('/predict', methods=['POST'])
def predict():
    response=""
    if 'image' in request.files:
        image = request.files['image']
        # Save the image to a desired location
        image.save('../images/image.jpg')
        # Perform further processing on the image
        # ...
        response = {
            'message': 'Image uploaded and processed successfully',
            'result': 'prediction result'
        }
    else:
        response={'message': 'No image file received'}
    return jsonify(response)

if __name__ == "__main__":
    flask_app.run(debug=True)
    flask_app.run(host="localhost",port=5000)