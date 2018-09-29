package com.gems.monitoring.function;

import com.gems.monitoring.domain.InstanceId;
import com.gems.monitoring.domain.MonitoredData;

public interface ResourceMonitoringAgent {

	MonitoredData getMonitoredDataForInstance( InstanceId instanceId);
	
	void submitReceivedMonitoringData(MonitoredData monitoringdata);
	
}
