package com.gems.monitoring.rma;

import com.gems.monitoring.commons.domain.InstanceId;
import com.gems.monitoring.commons.util.PrettyPrinter;
import com.gems.monitoring.function.ResourceMonitoringAgent;

public class MainTest {
	
	public static void main(String [] args) throws Exception {
		final InstanceId instanceId = new InstanceId("1");
		final ResourceMonitoringAgent rma = new ResouceMonitoringAgentImpl(instanceId);
		for(int i = 0 ; i < 100 ; i++ ) {
			System.out.println(PrettyPrinter.printMonitoringData(rma.getMonitoredDataForInstance(instanceId)));
			Thread.sleep(2000);
		}
	}

}
