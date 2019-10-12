# Azure Water Monitoring System

The purpose of this example is to demonstrate the use of Azure storage options.
It consists of the following:
1) Monitoring Station
2) Flood Warning Service
3) Notification Service
4) API Gateway

For simplicity, there is no security or cloud services.
Here is brief description of the systems:

## Monitoring Stations
Uses a sensor to measure river level.

### API
* PUT void stationPreference: StationPreferences preferences
* GET StationPreferences stationPreference: void
* GET Observation observation: void

## Flood Warning Service
Uses data collected from monitoring stations to detect flood stage:
* 5 - Major Flooding
* 4 - Moderate Flooding
* 3 - Minor Flooding
* 2 - Near Flood Stage
* 1 - No Flooding
* 0 - At or Below Low Water Threshold

### API
* POST monitoringStation createMonitoringStations: MonitoringStation monitoringStation
* monitoringStation putMonitoringStations: MonitoringStation monitoringStation
* StationPreferences getStationPreferences: String stationId

## Notification Service

### API
* void addRecipient: NotificationPreferences notificationPreferences
* void removeRecipient: String emailAddress

### Events
* notificationSent

## API Gateway

### API
* void changeStationPreferences: StationPreferences preferences
* StationPreferences getStationPreferences: String stationId
* List riverForecast: String stationId
* void addRecipient: NotificationPreferences notificationPreferences
* void removeRecipient: String emailAddress

### Events
* stationOnline
* stationOffline
* measurementObserved

# Azure Setup

Follow the instructions to install the Azure CLI and login.
```
az login
```

Create a Resource group for this project. Use the following command to retrieve a
list of locations:
```
az account list-locations -o table
```

## Create a Resource Group
Create a resource group with the location of your choice. I called mine
"azure-training" for this exercise.
```
export GROUP=azure-training
az group create --name ${GROUP} --location centralus
```

## Create a Servicebus Namespace
Next, create a service bus namespace. The name must be unique across Azure. The namespace "rivers"
was available at the time of this writing. Try adding numbers to "rivers" or come up with a name of your
choice.
```
export NAMESPACE=rivers
az servicebus namespace create --resource-group ${GROUP}  --name ${NAMESPACE}
```

## Create a Servicebus Topic
Create a topic for river observation messages. For this exercise it is called "RiverObservationsTopic".
```
export RIVERS_TOPIC=RiverObservationsTopic
az servicebus topic create --resource-group ${GROUP} \
    --namespace-name ${NAMESPACE} \
    --name ${RIVERS_TOPIC}
```

## Create a Topic Subscription
Create a subscription to the top.
```
az servicebus topic subscription create --resource-group ${GROUP}  \
    --namespace-name ${NAMESPACE} --topic-name ${RIVERS_TOPIC} \
    --name FloodMonitoring
```

## Create the Authorization rules

### RootManageSharedAccessKey Automatically Created
This isn't actually needed for this project. Use this command to see it.
```
az servicebus namespace authorization-rule keys list --resource-group ${GROUP} \
 --namespace-name ${NAMESPACE} --name RootManageSharedAccessKey
```
### Create a Read/Write Authorization rule
```
az servicebus namespace authorization-rule create --resource-group ${GROUP} \
 --namespace-name ${NAMESPACE} --name ReadWrite --rights Listen Send
```

Lookup the ReadWrite primaryConnectionString to use in the application configuration.
```
 az servicebus namespace authorization-rule keys list --resource-group ${GROUP} \
  --namespace-name ${NAMESPACE} --name ReadWrite --query 'primaryConnectionString' -o json

"Endpoint=sb://rivers.servicebus.windows.net/;SharedAccessKeyName=ReadWrite;SharedAccessKey=B0B1lAicVuOaq2SCC4+EE2xYwonYn8Wl80eIycG0jfY="
```

### Create a Read-only Authorization rule
```
az servicebus namespace authorization-rule create --resource-group ${GROUP} \
 --namespace-name ${NAMESPACE} --name ReadOnly --rights Listen
```

Lookup the ReadWrite primaryConnectionString to use in the application configuration.
```
 az servicebus namespace authorization-rule keys list --resource-group ${GROUP} \
  --namespace-name ${NAMESPACE} --name ReadOnly --query 'primaryConnectionString' -o json

"Endpoint=sb://rivers.servicebus.windows.net/;SharedAccessKeyName=ReadOnly;SharedAccessKey=53cOWDfM3g0zukbnG6ID9SbDd+MFC/y/7jbTCghVts4="
```
