# -*- coding: utf-8 -*-
"""Final of IBRL Dataset Anomaly Detection (Temp)
"""

# Commented out IPython magic to ensure Python compatibility.
import pandas as pd
import numpy as np
from pandas import Series as sr, DataFrame as df
import matplotlib.pyplot as plt
import seaborn as sns
from tensorflow import keras
from tensorflow.keras import layers


data = pd.read_csv("data.txt", sep = ' ', header = None)
print(data.head())
print(data.describe())

data.rename(columns = {0:'date',1:'time',2:'epochId',3:'moteId',4:'temp',5:'humidity',6:'light',7:'voltage'},inplace=True)

data = data.sort_values(['date','time'],ascending = [1,1])
print(data.head())

print(data["epochId"].unique())

data = data[(data.moteId<55)]
for item in data["moteId"].unique():
  print(item)

for key,d in data.groupby("moteId"):
    print(key,len(d.index),"\n",d.head())
    print("--------------------------------")

sns.displot(data, x="temp", binwidth=5)

import plotly.express as px
fig = px.histogram(data, x="temp")
fig.show()

sns.displot(data, x="humidity", binwidth=100)

import plotly.express as px
fig = px.histogram(data, x="humidity")
fig.show()

d1=data[data["moteId"].isin([1])]

print(d1.head())
len(d1)
d1.to_csv('data_out.csv',index=False)

print(d1.head())

# make sure date column is date as string dtype
d1['date'] = pd.to_datetime(d1['date']).dt.date.astype(str)

# make sure time column is of string dtype
d1['time'] = d1['time'].astype(str)

# combine date and time column (now both string dtype), then parse to datetime
d1['datetime'] = pd.to_datetime(d1['date'] + ' ' + d1['time'])

print(d1.head())

ds=d1.groupby(['datetime']).mean().reset_index()
print(ds.head())
ds=ds.set_index(['datetime'])
print(ds.head())
print(d1.head())

ds2=ds["temp"]
print(ds2.head())

sns.relplot(data=ds2, kind="line", height = 8, aspect = 4)

first_filtered_dates=ds2.loc['2004-02-29':'2004-03-09']
first=ds2.loc['2004-03-11':'2004-03-14']
second=ds2.loc['2004-03-17':'2004-03-30']
second_filtered_dates=pd.concat([first, second])

print(first_filtered_dates.loc['2004-03-10':'2004-03-10'])

first_filtered_dates.drop(first_filtered_dates.loc['2004-03-10':'2004-03-10'].index, inplace=True)
second_filtered_dates.drop(second_filtered_dates.loc['2004-03-10':'2004-03-10'].index, inplace=True)

print(first_filtered_dates.loc['2004-03-10':'2004-03-10'])

print(second_filtered_dates.loc['2004-03-10':'2004-03-10'])

print(first_filtered_dates)

print(second_filtered_dates)

plt.style.use('dark_background')
g=sns.relplot(data=first_filtered_dates, kind="line", height = 8, aspect = 4)
g.fig.suptitle('Training Data (Temp)', fontsize=24)

g=sns.relplot(data=second_filtered_dates, kind="line", height = 8, aspect = 4)
g.fig.suptitle('Testing Data (Temp)', fontsize=24)

first_agg_5m = first_filtered_dates.groupby(pd.Grouper(freq='30Min')).aggregate(np.mean)

first_agg_5m.drop(first_agg_5m.loc['2004-03-10':'2004-03-10'].index, inplace=True)

print(first_agg_5m)

second_agg_5m = second_filtered_dates.groupby(pd.Grouper(freq='30Min')).aggregate(np.mean)

second_agg_5m.drop(second_agg_5m.loc['2004-03-15':'2004-03-16'].index, inplace=True)

print(second_agg_5m)

training_mean = first_agg_5m.mean()
training_std = first_agg_5m.std()
df_training_value = (first_agg_5m - training_mean) / training_std

print("Number of training samples:", len(df_training_value))
print(df_training_value.shape)

print(training_mean)

TIME_STEPS = 48

# Generated training sequences for use in the model.
def create_sequences(values, time_steps=TIME_STEPS):
    output = []
    for i in range(len(values) - time_steps + 1):
        output.append(values[i : (i + time_steps)])
    return np.stack(output)


x_train = create_sequences(df_training_value.values)
print("Training input shape: ", x_train.shape)

x_train=np.reshape(x_train,(433, 48, 1))

print(first_agg_5m.isnull().values.any())

check_nan_in_df = first_agg_5m.isnull()
print(check_nan_in_df)

check_nan_in_df.to_csv('check_nan_in_df.csv',index=True)

check_nan_in_df_sec = second_agg_5m.isnull()
print (check_nan_in_df_sec)

check_nan_in_df_sec.to_csv('check_nan_in_df_sec.csv',index=True)

print(first_agg_5m.isnull().sum().sum())

print(second_agg_5m.isnull().sum().sum())

print(x_train[0])

model = keras.Sequential(
    [
        layers.Input(shape=(x_train.shape[1], x_train.shape[2])),
        layers.Conv1D(
            filters=32, kernel_size=7, padding="same", strides=2, activation="relu"
        ),
        layers.Dropout(rate=0.2),
        layers.Conv1D(
            filters=16, kernel_size=7, padding="same", strides=2, activation="relu"
        ),
        layers.Conv1DTranspose(
            filters=16, kernel_size=7, padding="same", strides=2, activation="relu"
        ),
        layers.Dropout(rate=0.2),
        layers.Conv1DTranspose(
            filters=32, kernel_size=7, padding="same", strides=2, activation="relu"
        ),
        layers.Conv1DTranspose(filters=1, kernel_size=7, padding="same"),
    ]
)
model.compile(optimizer=keras.optimizers.Adam(learning_rate=0.001), loss="mse")
print(model.summary())

history = model.fit(
    x_train,
    x_train,
    epochs=50,
    batch_size=64,
    validation_split=0.1,
    callbacks=[
        keras.callbacks.EarlyStopping(monitor="val_loss", patience=5, mode="min")
    ],
)

print(history.history)

sns.relplot(data=history.history, kind="line", height = 4, aspect = 4)

plt.style.use('dark_background')
# Get train MAE loss.
x_train_pred = model.predict(x_train)
train_mae_loss = np.mean(np.abs(x_train_pred - x_train), axis=1)

plt.hist(train_mae_loss, bins=50)
plt.xlabel("Train MAE loss")
plt.ylabel("No of samples")
plt.title("Reconstruction Error")
plt.show()

# Get reconstruction loss threshold.
threshold = np.max(train_mae_loss)
print("Reconstruction error threshold: ", threshold)

# Checking how the first sequence is learnt
plt.plot(x_train[0])
plt.plot(x_train_pred[0])
plt.show()

df_test_value = (second_agg_5m - training_mean) / training_std
fig, ax = plt.subplots()
df_test_value.plot(legend=False, ax=ax)
plt.show()

# Create sequences from test values.
x_test = create_sequences(df_test_value.values)
print("Test input shape: ", x_test.shape)
x_test=np.reshape(x_test,(798, 48, 1))

# Get test MAE loss.
x_test_pred = model.predict(x_test)
test_mae_loss = np.mean(np.abs(x_test_pred - x_test), axis=1)
test_mae_loss = test_mae_loss.reshape((-1))

plt.hist(test_mae_loss, bins=50)
plt.xlabel("test MAE loss")
plt.ylabel("No of samples")
plt.show()

# Detect all the samples which are anomalies.
anomalies = test_mae_loss > threshold
print("Number of anomaly samples: ", np.sum(anomalies))
print("Indices of anomaly samples: ", np.where(anomalies))

print("Count of Anomalous Points: ", np.sum(anomalies))
print("Anomalous Points IDs: ")
for i in np.where(anomalies):
    print(i, end = ' ')

# Checking how the first sequence is learnt
plt.plot(x_train[0])
plt.plot(x_train_pred[0])
plt.title('Real label vs. Prediction for 1st Sequence')
plt.ylabel('Damped oscillation')
plt.show()

# data i is an anomaly if samples [(i - timesteps + 1) to (i)] are anomalies
anomalous_data_indices = []
for data_idx in range(TIME_STEPS - 1, len(df_test_value) - TIME_STEPS + 1):
    if np.all(anomalies[data_idx - TIME_STEPS + 1 : data_idx]):
        anomalous_data_indices.append(data_idx)

df_subset = second_agg_5m.iloc[anomalous_data_indices]
fig, ax = plt.subplots()
second_agg_5m.plot(legend=False, ax=ax)
df_subset.plot(legend=False, ax=ax, color="r")
plt.title("Anomalous Data")
plt.show()

plt.style.use('dark_background')
df_test_value = (second_agg_5m - training_mean) / training_std
fig, ax = plt.subplots()
df_test_value.plot(legend=True, ax=ax)
plt.show()

df_subset = second_agg_5m.iloc[anomalous_data_indices]
fig, ax = plt.subplots()
second_agg_5m.plot(legend=False, ax=ax, color="b")
df_subset.plot(legend=False, ax=ax, color="r")
plt.title('Temperature trend')
plt.ylabel('Temperature')
plt.show()

fig, ax = plt.subplots()
first_filtered_dates.plot(legend=True, ax=ax)
plt.title("Training Data (Temp)")
plt.show()
fig, ax = plt.subplots()
second_filtered_dates.plot(legend=True, ax=ax)
plt.title("Testing Data (Temp)")
plt.show()


print(first_filtered_dates)

print(first_filtered_dates.index[0])

print(first_filtered_dates[first_filtered_dates.index[0]])

print(first_filtered_dates.index)

data=pd.DataFrame({'date':first_filtered_dates.index, 'temp':first_filtered_dates.values})

data

data.to_csv('data_file.csv',index=False)

pd.DataFrame(second_agg_5m).to_csv('second_agg_5m.csv',index=True)
