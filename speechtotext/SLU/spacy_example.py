import spacy
nlp = spacy.load("en_core_web_lg")
doc = nlp(u'Smart-home turn on the window')
print(doc.ents)
for ent in doc.noun_chunks:
    print(ent.text)

for word in doc:
    print('word.dep:', word.dep, ' | ', 'word.dep_:', word.dep_)

for x in doc :
    if x.pos_ == "NOUN" or x.pos_ == "PROPN" or x.pos_=="PRON":
	    print(x)
