
import tensorflow as tf
import numpy as np
import pandas as pd

def Load_plant_Leaf_disease_Model(pth): 

  new_model = tf.keras.models.load_model(pth)
  # Check its architecture
  #new_model.summary()
  return new_model

def load_prep(img_path):
  img = tf.io.read_file(img_path)

  img = tf.image.decode_image(img)

  img = tf.image.resize(img,size=(224,224))
  return img

#Read disease Names
def get_class_names_plants_leaf(path_file):
  path=path_file
  npzFileClassNames = np.load(path)
  class_Names=npzFileClassNames['class_names']
  return class_Names

#Prediction by Name
def Prediction_result(Model, image,class_Names):

  pred = Model.predict(tf.expand_dims(image,axis=0))
  Index= pred.argmax()
  predicted_class = class_Names[Index]

  return predicted_class, pred[0][Index]*100


def Prediction_result_for_pests(Model, image):

  pred = Model.predict(tf.expand_dims(image,axis=0))
  Index= pred.argmax()

  return  Index, pred[0][Index]*100


def classe_names_pests(pathpests):
    f = open(pathpests)
    label = []
    name = []
    for line in f.readlines():
        label.append(int(line.split()[0]))
        name.append(' '.join(line.split()[1:]))
    classes = pd.DataFrame([label, name]).T
    classes.columns = ['label','name']

    return classes
  