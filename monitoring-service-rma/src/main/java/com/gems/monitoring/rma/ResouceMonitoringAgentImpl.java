package com.gems.monitoring.rma;

import com.gems.monitoring.commons.domain.InstanceId;
import com.gems.monitoring.commons.domain.MonitoredData;
import com.gems.monitoring.function.ResourceMonitoringAgent;
import com.gems.monitoring.rma.datastore.MonitoringDataStore;
import com.gems.monitoring.rma.datastore.impl.InMemoryDataStore;
import com.gems.monitoring.rma.scheduler.ResourceCheckScheduler;

public class ResouceMonitoringAgentImpl implements ResourceMonitoringAgent {

	private final MonitoringDataStore dataStore = new InMemoryDataStore();
	
	public ResouceMonitoringAgentImpl( final InstanceId instanceId ) {
		final ResourceCheckScheduler scheduler = new ResourceCheckScheduler();
		scheduler.scheduleResourceCheck(instanceId, dataStore);
	}
	
	@Override
	public MonitoredData getMonitoredDataForInstance(final InstanceId instanceId ) {
		return dataStore.retrieveMonitoringDataForInstance(instanceId);
	}

	@Override
	public void submitReceivedMonitoringData(final MonitoredData monitoringdata) {
		dataStore.storeMonitoringDataForInstance(monitoringdata);
	}

}
