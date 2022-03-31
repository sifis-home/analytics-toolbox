import pandas as pd
import numpy as np
import spacy
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import classification_report, confusion_matrix
import pickle

#Replace dataset_path with the dataset you want to use
df = pd.read_csv(dataset_path)
queries = np.array(df['text'])
labels = np.array(df['intent'])

# Install trained pipeline with command 'python -m spacy download en_core_web_lg'
nlp = spacy.load('en_core_web_lg')

n_queries = len(queries)
dim_embedding = nlp.vocab.vectors_length
X = np.zeros((n_queries, dim_embedding))

for idx, sentence in enumerate(queries):
    doc = nlp(sentence)
    X[idx, :] = doc.vector
X_train, X_test, y_train, y_test = train_test_split(X, labels, test_size=0.20, random_state=42)
neigh = KNeighborsClassifier(n_neighbors=1)
neigh.fit(X_train, y_train)

#Save the weights in neighpickle_file
neigh_pickle = open('neighpickle_file', 'wb')
pickle.dump(neigh, neigh_pickle)

print("Accuracy on Test Set: ", np.count_nonzero(neigh.predict(X_test) == y_test)/len(y_test))

y_true = labels
y_pred = []
for text in queries:
    #If you want to find out every wrong prediction, uncomment the two prints
    #print(text)
    doc = nlp(text)
    prediction = neigh.predict(doc.vector.reshape(1, -1))
    #print(prediction)
    y_pred.append(prediction)

print(classification_report(y_true, y_pred))
print(confusion_matrix(y_true, y_pred))
