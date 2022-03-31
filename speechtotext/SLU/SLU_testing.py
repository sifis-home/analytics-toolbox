import spacy
import os
import pickle

voice_command = 'Play the music'
  
nlp = spacy.load("en_core_web_lg")
neigh = pickle.load(open('neighpickle_file', 'rb'))
doc = nlp(voice_command)
subdoc = doc
prediction = neigh.predict(doc.vector.reshape(1, -1))
print(prediction)

# If you're not using subdatasets version, comment all the following code.

if prediction == ' ManageLights':
    subneigh = pickle.load(open('lightspickle_file', 'rb'))
elif prediction == ' ManageTemperature':
    subneigh = pickle.load(open('temperaturepickle_file', 'rb'))
elif prediction == ' ManageVolume':
    subneigh = pickle.load(open('volumepickle_file', 'rb'))
else:
    subneigh = pickle.load(open('appliancespickle_file', 'rb'))

subprediction = subneigh.predict(subdoc.vector.reshape(1, -1))
print(subprediction)
