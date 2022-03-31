# Spoken Language Understanding

## SETUP

```
cd SpeechToText/SLU
virtualenv env -p python3
source env/bin/activate
pip install -r requirements.txt
```

Install the trained pipeline:
```
python3 -m spacy download en_core_web_lg
```
Download custom datasets [here](https://bit.ly/3dWz1CG).

## RUN

Train the classifier using SLU_training.py script. You have to indicate which dataset you want to use for training.

```
python3 SLU_training.py
```

Edit and run SLU_testing.py, indicating pickle files (containing the weights) created during the training.

```
python3 SLU_testing.py
```

