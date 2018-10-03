package com.gems.monitoring.rma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gems.monitoring.domain.Configurations;
import com.gems.monitoring.domain.InstanceId;
import com.gems.monitoring.domain.MonitoredData;
import com.gems.monitoring.function.ResourceMonitoringAgent;
import com.gems.monitoring.rma.datastore.MonitoringDataStore;
import com.gems.monitoring.rma.datastore.impl.InMemoryDataStore;
import com.gems.monitoring.rma.scheduler.ResourceCheckScheduler;

public class ResouceMonitoringAgentImpl implements ResourceMonitoringAgent {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final MonitoringDataStore dataStore = new InMemoryDataStore();
	
	@Override
	public MonitoredData getMonitoredDataForInstance(final InstanceId instanceId ) {
		return dataStore.retrieveMonitoringDataForInstance(instanceId);
	}

	@Override
	public void submitReceivedMonitoringData(final MonitoredData monitoringdata) {
		dataStore.storeMonitoringDataForInstance(monitoringdata);
	}

	@Override
	public void initialize(final Configurations config) {
		logger.debug("Initializing the Resource Monitoring Agent with configurations : "+config);
		final ResourceCheckScheduler scheduler = new ResourceCheckScheduler();
		scheduler.scheduleResourceCheck(config.getInstanceId(), dataStore);
		logger.debug("Resource Monitoring Agent initialized");
	}

}
