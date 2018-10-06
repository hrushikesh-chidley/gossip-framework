package com.monitoring.extension.datastore;

import java.util.Set;

import com.framework.gossip.domain.InstanceId;
import com.monitoring.extension.domain.MonitoredData;

public interface MonitoringDataStore {
	
	public void storeMonitoringDataForInstance(MonitoredData monitoredData);
	
	public MonitoredData retrieveMonitoringDataForInstance( InstanceId instanceId );

	public Set<MonitoredData> retrieveCompleteMonitoringData();
}
