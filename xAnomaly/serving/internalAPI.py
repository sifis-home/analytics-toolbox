#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pandas as pd
import requests
import time

def server_to_influx_org_service(server_name):
    
    if server_name == 'https://beta.yggio.net':
        org = "e92df36d3f5ac92d"
    if server_name == 'https://yggio3-beta.sensative.net':
        org='f48e11ad852e70bb'
    if server_name == 'https://kraftringen.yggio.net':
        org='75d4bcf113cfeb4d'    
    
    return org


def api_get(call, server, username, password, mySession):
    while True:
        response = mySession.get(call)
        if response.status_code == 200:
            return response
        elif response.status_code == 401:
            myHeaders = authorize(server, username, password)
            mySession.headers.update(myHeaders)
            print("Updated headers!")
        else:
            print("No response: ", response)
            time.sleep(60)
    
def collectOnePeriodOneNode(nodeId, measurement, starttime, endtime, server, username, password, mySession, **kwargs):
    # Seems like API always give you the first value in db if not specified start
    
    myHeaders = kwargs.get('myHeaders', None) #Backwards compability
    Dev = kwargs.get('dev', False)
    filename = kwargs.get('filename', None)
    lineno = kwargs.get('lineno', None)
    
    if Dev:
        df = pd.read_csv('example_training_data/data_from_'+filename+'_'+str(lineno)+'.csv')
    else:
        # Initial call 
        response = api_get(server + '/iotnodes/' + nodeId + '/stats?measurement=' + measurement +
        '&end=' + str(endtime), server, username, password, mySession)   
        jsonResponse = response.json()
        df = pd.json_normalize(jsonResponse)
        startTimeLastCall = pd.to_datetime(min(df.time)) 
        endTimeLastCall = pd.to_datetime(max(df.time)) 
        startTimeLastCallUnix = int(startTimeLastCall.timestamp()*1000)
        
        
        maxIter = 100
        maxCounter = 0
        while (startTimeLastCallUnix > starttime) and (maxCounter < maxIter):
            response = api_get(server + '/iotnodes/' + nodeId + '/stats?measurement=' + measurement +
            '&end=' + str(startTimeLastCallUnix), server, username, password, mySession)  
            jsonResponse = response.json()
            if jsonResponse:        # Catches if return is succesfull but an empty list
                dfLastCall = pd.json_normalize(jsonResponse)
                startTimeLastCall = pd.to_datetime(min(dfLastCall.time)) 
                endTimeLastCall = pd.to_datetime(max(dfLastCall.time)) 
                startTimeLastCallUnix = int(startTimeLastCall.timestamp()*1000)
                # df = df.append(dfLastCall, ignore_index=True)  
                df = pd.concat([df, dfLastCall])                  
            else:
                break
            maxCounter = maxCounter+1
        
        # Clean    
        df.drop_duplicates(inplace=True)
        df.time = pd.to_datetime(df.time)
        df.set_index('time', inplace=True)
    
    # Removes timepoints outside requested period
    df = df[df.index >= pd.to_datetime(starttime, origin='unix', unit='ms', utc=True)]
    df = df[df.index <= pd.to_datetime(endtime, origin='unix', unit='ms', utc=True)]
    
    #Save example data for debug purpose
    if filename:
        df[0:10].to_csv('example_training_data/data_from_'+filename+'_'+str(lineno)+'.csv')

    return df

    
def collectDataFromManyNodes(graph, seriesIdx, measurement, starttime, endtime, server, username, password, mySession):

    """ By fromManyNodes I mean two nodes that measures the same thing, for example two themometers sitting next two eachother.
    The function outputs a data frame with one series.
    """

    nrOfNodeIds=len(graph['series'][seriesIdx]['nodeId'])

    df = pd.DataFrame()
    for nodeIdx in range(nrOfNodeIds):

        nodeId = graph['series'][seriesIdx]['nodeId'][nodeIdx]
        
        # Collect data
        dfPart = collectOnePeriodOneNode(nodeId, measurement, starttime, endtime, server, username, password, mySession)
        df=pd.concat([df, dfPart])
   
    return df


def authorize(server, username, password, **kwargs): 
    
    Dev = kwargs.get('dev', False)
            
    if not Dev:
        
        try:
            response = requests.post(server +'/auth/local', json={"username": username,"password": password})
            authorization = response.json()
            token = authorization['token']
            myHeaders = {'Authorization' : 'Bearer ' + token + ''}
        except Exception:
            print("Cannot authorize")
            myHeaders = {'Authorization' : 'Bearer '+'***no token since cannot authorize***'+''}
            
    elif Dev:
        myHeaders = {'Authorization' : 'Bearer '+'***no token since in dev mode***'+''}
        
    return myHeaders
    
   
def get_all_node_ids(server, username, password, mySession, **kwargs):
    
    Dev = kwargs.get('dev', False)
    
    df = pd.DataFrame()
    
    if not Dev:
        try:
            response = api_get(server +'/iotnodes', server, username, password, mySession) 
            jsonResponse = response.json()
            df = pd.json_normalize(jsonResponse)
        except Exception:
            print("Cannot get list of IDs")
    elif Dev:
            print("Fix so that you can load example data")
        
    return df
