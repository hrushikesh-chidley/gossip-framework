package com.monitoring.extension.datastore.impl;

import java.util.HashMap;
import java.util.Map;

import com.framework.gossip.domain.InstanceId;
import com.monitoring.extension.datastore.MonitoringDataStore;
import com.monitoring.extension.domain.MonitoredData;

public class InMemoryDataStore implements MonitoringDataStore {

	private Map<InstanceId, MonitoredData> dataStore = new HashMap<>(500);
	
	@Override
	public void storeMonitoringDataForInstance(final MonitoredData monitoredData) {
		dataStore.put(monitoredData.getInstanceId(), monitoredData);
	}

	@Override
	public MonitoredData retrieveMonitoringDataForInstance(final InstanceId instanceId) {
		final MonitoredData emptyData = new MonitoredData();
		emptyData.setInstanceId(instanceId);
		
		final MonitoredData data = dataStore.get(instanceId);
		return data == null ?  emptyData : data;
	}
}
