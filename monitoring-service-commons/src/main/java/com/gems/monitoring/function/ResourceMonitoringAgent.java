package com.gems.monitoring.function;

import com.gems.monitoring.domain.Configurations;
import com.gems.monitoring.domain.InstanceId;
import com.gems.monitoring.domain.MonitoredData;

public interface ResourceMonitoringAgent {
	
	void initialize(Configurations config);

	MonitoredData getMonitoredDataForInstance( InstanceId instanceId);
	
	void submitReceivedMonitoringData(MonitoredData monitoringdata);
	
}
