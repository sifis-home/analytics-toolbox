# SPEECH TO TEXT

## SETUP

``` 
cd SpeechToText/API_service/
virtualenv env -p python3
source env/bin/activate
pip install -r requirements.txt
```

Download the pre-trained model  
`wget https://github.com/mozilla/DeepSpeech/releases/download/v0.9.3/deepspeech-0.9.3-models.pbmm`  
Download the external scorer  
`wget https://github.com/mozilla/DeepSpeech/releases/download/v0.9.3/deepspeech-0.9.3-models.scorer`

## RUN

`python app.py`

The service is available on http://localhost:9999/flaskAPI/

## CREDIT

Mozilla [DeepSpeech](https://github.com/mozilla/DeepSpeech)
