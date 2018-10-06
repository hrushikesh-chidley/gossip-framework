package com.gems.monitoring.rma;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gems.monitoring.config.Configurations;
import com.gems.monitoring.domain.InstanceId;
import com.gems.monitoring.function.GossipMessagePayloadAgent;
import com.gems.monitoring.rma.datastore.MonitoringDataStore;
import com.gems.monitoring.rma.datastore.impl.InMemoryDataStore;
import com.gems.monitoring.rma.domain.MonitoredData;
import com.gems.monitoring.rma.scheduler.ResourceCheckScheduler;

public class ResouceMonitoringAgentImpl implements GossipMessagePayloadAgent<MonitoredData> {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final MonitoringDataStore dataStore = new InMemoryDataStore();
	
	public void initialize(final Configurations config) {
		logger.debug("Initializing the Resource Monitoring Agent with configurations : "+config);
		final ResourceCheckScheduler scheduler = new ResourceCheckScheduler();
		scheduler.scheduleResourceCheck(config.getInstanceId(), dataStore);
		logger.debug("Resource Monitoring Agent initialized");
	}

	@Override
	public MonitoredData getPayloadDataForInstance(InstanceId instanceId) {
		return dataStore.retrieveMonitoringDataForInstance(instanceId);
	}

	@Override
	public void submitReceivedPayloadData(Serializable payloadData) {
		MonitoredData data = (MonitoredData)payloadData;
		dataStore.storeMonitoringDataForInstance(data);		
	}

}
