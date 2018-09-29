package com.gems.monitoring.rma.scheduler;

import java.util.TimerTask;

import com.gems.monitoring.domain.InstanceId;
import com.gems.monitoring.domain.MonitoredData;
import com.gems.monitoring.rma.datastore.MonitoringDataStore;
import com.gems.monitoring.rma.sensor.Sensors;

public class ScheduledResourceCheck extends TimerTask {

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
		final MonitoredData monitoredData = new MonitoredData();
		monitoredData.setInstanceId(instanceId);
		Sensors.getAllSensors().forEach( sensor -> {
			monitoredData.addToMonitoredData(sensor.senseResourceData());
		});
		dataStore.storeMonitoringDataForInstance(monitoredData);
	}
	
}
