# Automatic build and deploy
The subfolder .github/workflows needs to be in the root of the project. The subfolder .github/workflows is a template for a ci/cd-workflow and variables needs to be changed according to the setup of the repo.

# Manual build and deploy
```
docker build -f Dockerfile -t ghcr.io/sifis-home/xanomaly:latest .
docker-compose up -d
docker-compose down
```

# What is this?
The overall goal is to detect intrusion attempts by creating an anomaly detection system for IoT data. Anomaly detection is the process of identifying unusual patterns in data that deviate from normal behavior. Supervised anomaly detection is a type of anomaly detection that learns from labelled data and can be used to identify previously seen anomalies. On the other hand, unsupervised anomaly detection learns from unlabelled data and can be used to identify previously unseen anomalies. As new intrusion attempts are likely to differ from previous ones, an anomaly detection system for IoT data needs to be able to handle previously unseen anomalies. Therefore, we have chosen to work with unsupervised models. We have chosen to base our system on autoencoders (AEs), a standard choice in the industry.

This PoC is an autoencoder model that takes infrared light readings from a sensor under an office desk as the input time series. The choice is motivated by the ease of testing deployment, both software engineering- and model-related, from an easily accessible sensor. In this PoC, SIFIS Home Cloud Interface handles most of the functionality. The input time series is streamed in real-time via an MQTT channel to the Python-based Docker container that consitutes this repo. The container uses the Paho MQTT client library to receive input data points and the Tensorflow Keras library to query a trained AE-model. If an anomaly is detected, an MQTT message is sent back to the SIFIS Home Cloud Interface and stored in an output time series.
