package com.monitoring.extension;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.framework.gossip.GossipMessagePayloadAgent;
import com.framework.gossip.common.Configurations;
import com.framework.gossip.domain.InstanceId;
import com.monitoring.extension.datastore.MonitoringDataStore;
import com.monitoring.extension.datastore.impl.InMemoryDataStore;
import com.monitoring.extension.domain.MonitoredData;
import com.monitoring.extension.scheduler.ResourceCheckScheduler;

public class ResouceMonitoringAgentImpl implements GossipMessagePayloadAgent<MonitoredData> {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final MonitoringDataStore dataStore = new InMemoryDataStore();
	
	public void initialize(final Configurations config) {
		logger.debug("Initializing the Resource Monitoring Agent with Configuration "+config);
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
