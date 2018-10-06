package com.monitoring.extension.scheduler;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.framework.gossip.domain.InstanceId;
import com.monitoring.extension.datastore.MonitoringDataStore;
import com.monitoring.extension.domain.MonitoredData;
import com.monitoring.extension.sensor.Sensors;

public class ScheduledResourceCheck extends TimerTask {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final InstanceId instanceId;

	private final MonitoringDataStore dataStore;
	
	public ScheduledResourceCheck( final InstanceId instanceId, final MonitoringDataStore dataStore ) {
		this.instanceId = instanceId;
		this.dataStore = dataStore;
	}
	
	@Override
	public void run() {
		runScheduledCheck();
	}

	private void runScheduledCheck() {
		logger.debug("Running scheduled resource check!!");
		final MonitoredData monitoredData = new MonitoredData();
		monitoredData.setInstanceId(instanceId);
		Sensors.getAllSensors().forEach( sensor -> {
			monitoredData.addToMonitoredData(sensor.senseResourceData());
		});
		logger.debug("Scheduled resource check finished");
		dataStore.storeMonitoringDataForInstance(monitoredData);
	}
	
}
