# Device Fault Detection Analytic

Faulty devices can either affect the whole performance of a system by getting disconnected or by staying connected to the network while not performing as expected and sending wrong readings to intended data recipients. The aim of this analytic is to ensure network reliability, service availability, good performance, and better monitoring of connected devices for service restoring and correction action time minimization in case an anomaly has been detected. 

Device fault detection uses time series data collected from sensors such as readings of humidity, temperature, noise, vibration, and air flows. Then this analytic processes these data and flags abnormal data deviations based on a specific threshold defined after the training phase leveraging readings during normal device behaviour. The device fault detection analytic uses an unsupervised learning method since the anomaly that might occur is unknown and not expected. A type of artificial neural network trained on unlabelled dataset, namely Autoencoder, is used in this context.

The used dataset is Intel Berkeley Research Lab Sensor (IBRL) Dataset (http://db.csail.mit.edu/labdata/labdata.html), should be exported from the previous link and saved as "data.txt" of the same directory as the code. It contains information about data collected from 54 sensors deployed in the Intel Berkeley Research lab between February 28th and April 5th, 2004. This dataset includes a log of about 2.3 million readings collected from the sensors related to temperature, humidity, voltage, and light. Only the data related to temperature and humidity are used in the performed analytics.

## Files in this directory are as below:
- **First File "final_of_ibrl_dataset_anomaly_detection_(temp).py"**: For temperature anomaly detection using a normal autoencoder network.
- **Second File "final_of_ibrl_dataset_anomaly_detection_(humidity).py"**: For humidity anomaly detection using an autoencoder network with differential privacy.
- **Third File "dp_final_of_ibrl_dataset_anomaly_detection_(temp).py"**: For temperature anomaly detection using a normal autoencoder network.
- **Fourth File "dp_final_of_ibrl_dataset_anomaly_detection_(humidity).py"**: For humidity anomaly detection using an autoencoder network differential privacy.
