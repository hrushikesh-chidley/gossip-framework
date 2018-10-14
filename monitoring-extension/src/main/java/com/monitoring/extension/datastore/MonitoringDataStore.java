package com.monitoring.extension.datastore;

import com.framework.gossip.domain.InstanceId;
import com.monitoring.extension.domain.MonitoredData;

public interface MonitoringDataStore {
	
	/**
	 * Stores the provided data to data store.
	 * <br>
	 * The provided data must contain the key and value both to be used while storing. 
	 * @param monitoredData the data to be stored
	 */
	public void storeMonitoringDataForInstance(MonitoredData monitoredData);
	
	/**
	 * Retrieve the stored data based on the key i.e. instance id. If there is no data
	 * for this key, an empty data object (not null) is return 
	 * @param instanceId the key for which data is searched
	 * @return the stored data or empty data object 
	 */
	public MonitoredData retrieveMonitoringDataForInstance( InstanceId instanceId );

}
