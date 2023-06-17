import numpy as np
from flask import Flask, request, jsonify, render_template,send_file
import pickle
from model import processImg
from Crop_Recommendation import crop_prediction
from leaf_model import *
from gpt4all import GPT4All


flask_app = Flask(__name__)
leaf_disease="leaf_disease"
pest_disease="pest_disease"

Model=Load_plant_Leaf_disease_Model('../models/best_plant_model.h5')
pest_model=Load_plant_Leaf_disease_Model('../models/best_pests_model.h5')
class_NamesPests=classe_names_pests("../models/classes.txt") 
class_Names=get_class_names_plants_leaf("../models/class_names.npz") 

crop_recommendation_model_path = '../models/Crop_Recommendation.pkl'
crop_recommendation_model = pickle.load(open(crop_recommendation_model_path, 'rb'))

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
# Process images
@flask_app.route("/test", methods=["GET"])
def test():

    return jsonify({'message': 'No image file received'})

@flask_app.route('/predict', methods=['POST'])
def predict():
    if 'image' in request.files:
        image = request.files['image']
        image.save('../images/image.jpg')
        imageToPredict = load_prep('../images/image.jpg')
        type_value = request.form['type']
        # TO DO : add condition based on column
        if type_value==leaf_disease:
            Pred_class, percent=Prediction_result(Model, imageToPredict,class_Names)
            response={
            'Disease_Name':Pred_class ,
            'Percentage':percent
            }
            return jsonify(response)
        elif type_value==pest_disease:
            Pred_classpest,percentpest=Prediction_result_for_pests(pest_model, imageToPredict)

            response={'Disease_Name':class_NamesPests['name'][Pred_classpest], 
                            'Percentage' :percentpest}
            return jsonify(response)
        else:
            response={'message': 'No image file received'}
            return jsonify(response)
        
@flask_app.route('/soule', methods=['POST'])
def predict_soul():
    nitrogen = float(request.form['nitrogen'])
    phosphorous = float(request.form['phosphorous'])
    potassium = float(request.form['potassium'])
    pHLevel = float(request.form['pHLevel'])
    rainfall = float(request.form['rainfall'])
    temperature = float(request.form['temperature'])
    humidity = float(request.form['humidity'])
    print(nitrogen, phosphorous, potassium, temperature, humidity, pHLevel, rainfall)

    final_prediction=crop_prediction(crop_recommendation_model,nitrogen, phosphorous, potassium, temperature, humidity, pHLevel, rainfall)
    response_data = {
        'prediction': final_prediction,
        "image_name":final_prediction+".jpg",
        "description":"Crop would be a good choice for your farm"
    }
    return     jsonify(response_data)

@flask_app.route('/get_image', methods=['GET'])
def get_image():
    image_name = request.args.get('image_name')
    # Logic to generate or fetch the image file
    image_file_path = '/home/hassan/hackathon/Flask_API/images/crop/'+image_name  # Replace with the actual path to your image file

    # Send the image file as a response
    return send_file(image_file_path, mimetype='image/jpeg')


        
@flask_app.route('/chat', methods=['POST'])
def chat():
    nitrogen = request.form['message']
    response="Hello Yes i love you"
    return response




if __name__ == "__main__":
    # flask_app.run(debug=True)
    flask_app.run(host="0.0.0.0",port=5000)