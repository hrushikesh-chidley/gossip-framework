package com.monitoring.extension.scheduler;

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.framework.gossip.domain.InstanceId;
import com.monitoring.extension.datastore.MonitoringDataStore;

public class ResourceCheckScheduler {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public final void scheduleResourceCheck(final InstanceId instanceId, final MonitoringDataStore dataStore) {
		final int resourceCheckDelay = 5000;
		logger.debug("Scheduling the resources check every "+resourceCheckDelay+" milliseconds");
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new ScheduledResourceCheck(instanceId, dataStore), 5, resourceCheckDelay);
	}
}
