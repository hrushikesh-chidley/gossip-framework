package com.gems.monitoring.function;

import com.gems.monitoring.commons.domain.InstanceId;
import com.gems.monitoring.commons.domain.MonitoredData;

public interface ResourceMonitoringAgent {

	MonitoredData getMonitoredDataForInstance( InstanceId instanceId);
	
	void submitReceivedMonitoringData(MonitoredData monitoringdata);
	
}
