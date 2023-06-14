#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import numpy as np
import tensorflow as tf
from tensorflow import keras
import json
import os
import requests
import time
import paho.mqtt.client as mqtt


#Own modules
import internalAPI


#Set root
# if os.getcwd()[0:1]== '/': #You are on linux, eg. at work
#     workingPath = '/home/sm/ml//data_validation/serving'
# elif os.getcwd()[0:1]== 'C': #You are on Window, eg. at home
#     workingPath = 'C:/Users/sara/Documents/ml/data_validation/serving'
# os.chdir(workingPath)


def preprocess(numpy_array, min_val, max_val):
    inputs = (numpy_array - min_val) / (max_val - min_val) 
    return inputs

def update_inputs(inputs, value):
    inputs = np.append(inputs, [value])
    inputs = np.delete(inputs, 0)
    return inputs

def mae_loss(x,y):
    loss = np.mean(np.abs(y - x))
    return loss

def signal_anomaly():
    
    requests.post('https://yggio.sifis-home.eu/http-push/generic?identifier=secret', json={
      "secret": "SFSXANMLY681521",
      "result": 1,
      "sourceSensor": "xAnomaly Strips Presence"
    })
    #Need to reset state to 0 to get notifications next time
    time.sleep(2)
    requests.post('https://yggio.sifis-home.eu/http-push/generic?identifier=secret', json={
      "secret": "SFSXANMLY681521",
      "result": 0,
      "sourceSensor": "xAnomaly Strips Presence"
    })
    

def detect_anomaly(autoencoder, inputs, server, username, password, ouput_nodeId):
    reconstruction = autoencoder.predict( np.array( [inputs,] )  )
    loss = mae_loss(inputs, reconstruction)
    if loss >= ub:
        signal_anomaly()
    elif loss <= lb:
        signal_anomaly()
    else:
        requests.post('https://yggio.sifis-home.eu/http-push/generic?identifier=secret', json={
          "secret": "SFSXANMLY681521",
          "result": 0,
          "sourceSensor": "xAnomaly Strips Presence"
        })


    
def on_connect(client, userdata, flags, rc):
    #print('NU KÖR VI CONNECT FUNCTONEN')
    # This will be called once the client connects
    if rc == 0:
        #pass
        print("Connected to MQTT Broker!")
    else:
        print("Failed to connect, return code %d\n", rc)
    # Subscribe here!
    client.subscribe(topic)
    #print(topic)
    
def on_message(client, userdata, msg):
    #print(f"Message received [{msg.topic}]: {msg.payload}")
    #messages.append(msg.payload)
    values = msg.payload
    values = json.loads(values.decode('utf-8'))
    values = values['diff']['value']
    if measurement in values:
        #time = values['encodedData']['timestamp']
        value = values[measurement]
        new_inputs.append(value)
        value = preprocess(value, min_val, max_val)
        inputs = userdata
        inputs = update_inputs(inputs, value)
        print("New inputs", inputs)
        detect_anomaly(autoencoder, inputs, server, username, password, ouput_nodeId)
        client.user_data_set(inputs)


#Load params
models_path = 'model_specifications.json'
idx=0
models_spec = json.load(open(models_path))
model_spec = models_spec[idx]

#Model param
nodeId = model_spec['Data']['_id']
measurement = model_spec['Data']['measurement']
modelId = model_spec['modelId']
lb=np.float64(model_spec['anomaly_bound_lower'])
ub=np.float64(model_spec['anomaly_bound_higher'])
SEQUENCE_LENGTH=58

#Data param
server = model_spec['Data']['server']
username='myhome-administrator'
password='sfshm1!'
ouput_nodeId = model_spec['Data']['ouput_nodeId']

#Channel param
basicCredentialsSetId = model_spec['Data']['basicCredentialsSetId']
nodeId4last = nodeId[len(nodeId)-4:len(nodeId)]
channel_username='xAnomaly-mqtt'
channel_password='sfshm1!'
topic = 'yggio/output/v2/'+basicCredentialsSetId+'/iotnode/'+nodeId
server = model_spec['Data']['server']
mqqt_path = 'mqtt.'+ server[server.index("/")+2:len(server)].replace('/api', '')


# Stuff to do once at init

#Autorize  
myHeaders = internalAPI.authorize(server, username, password)
mySession = requests.Session()
mySession.headers.update(myHeaders)

#Decide data to load
starttime = 0
endtime = int(round(time.time() * 1000))

#Load data
df = internalAPI.collectOnePeriodOneNode(nodeId, measurement, starttime, endtime, server, username, password, mySession)
inputs=df[len(df)-SEQUENCE_LENGTH:len(df)].value.to_numpy()
 

#Preprocess
min_val = np.int64(model_spec['Normalization']['min'])
max_val = np.int64(model_spec['Normalization']['max'])
inputs = preprocess(inputs, min_val, max_val)

#Load model
autoencoder = keras.models.load_model('models/'+modelId)



# Indefnitie loop that reacts on each message from channel

new_inputs=[]    
client = mqtt.Client("mqqt-test-1", userdata=inputs) # client ID "mqtt-test"
client.tls_set()
client.on_connect = on_connect
client.on_message = on_message
client.username_pw_set(channel_username, channel_password)
client.user_data_set(inputs) 
client.connect(mqqt_path, 8883)
client.loop_forever()  # Start networking daemon

