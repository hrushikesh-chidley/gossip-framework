package com.gems.monitoring.rma.datastore;

import java.util.Set;

import com.gems.monitoring.domain.InstanceId;
import com.gems.monitoring.domain.MonitoredData;

public interface MonitoringDataStore {
	
	public void storeMonitoringDataForInstance(MonitoredData monitoredData);
	
	public MonitoredData retrieveMonitoringDataForInstance( InstanceId instanceId );

	public Set<MonitoredData> retrieveCompleteMonitoringData();
}
