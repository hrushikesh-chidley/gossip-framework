package com.gems.monitoring.rma.scheduler;

import java.util.Timer;

import com.gems.monitoring.domain.InstanceId;
import com.gems.monitoring.rma.datastore.MonitoringDataStore;

public class ResourceCheckScheduler {

	public final void scheduleResourceCheck(final InstanceId instanceId, final MonitoringDataStore dataStore) {
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new ScheduledResourceCheck(instanceId, dataStore), 5, 5000);
	}
}
