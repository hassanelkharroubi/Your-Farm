import os
import numpy as np
from keras.models import load_model
import pickle
import sklearn
import pandas as pd




# Give some values to these params

def crop_prediction(crop_model,N, P, K, temperature, humidity, ph, rainfall):

	data = np.array([[N, P, K, temperature, humidity, ph, rainfall]])
	my_prediction = crop_model.predict(data)
	final_prediction = my_prediction[0]

	return final_prediction

if __name__ == '__main__':

	'''
	N='Nitrogen (ratio)'
	P='Phosphorous(Ratio)'
	K='Potassium(Ratio)'
	ph='pH level'
	rainfall='Rainful(in mm)'
	temperature='Temperature (in °C)'
	humidity='Relative Humidity (in %)'

	'''

	final_prediction=crop_prediction(N=85, P=58, K=41, temperature=21, humidity=80, ph=7, rainfall=226 )
	print( final_prediction,' crop would be a good choice for your farm.')

	#SHow the image using this path  '/crop/'+final_prediction+'.JPG'
