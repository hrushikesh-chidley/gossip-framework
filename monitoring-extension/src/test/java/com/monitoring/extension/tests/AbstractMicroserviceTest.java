package com.monitoring.extension.tests;

import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.framework.gossip.GossipAgent;
import com.framework.gossip.common.Configurations;
import com.framework.gossip.domain.InstanceId;
import com.framework.gossip.domain.InstancesInfo;
import com.framework.gossip.error.GossipException;
import com.framework.gossip.impl.GossipAgentImpl;
import com.monitoring.extension.ResourceMonitoringAgentImpl;
import com.monitoring.extension.domain.MonitoredData;

public class AbstractMicroserviceTest {

	protected void startMicroservice( final String instanceId, final int port) throws GossipException {
		final Configurations config = new Configurations();

		config.setInstanceId(instanceId);
		config.setBroadcastIP("192.168.1.255");
		config.setLocalPort(port);
		config.setGossipDelay(200);
		config.setCleanupCount(15);
		config.setPartitionCount(200);

		final GossipAgent gossipAgent = new GossipAgentImpl();
		final ResourceMonitoringAgentImpl resourceMonitorinAgent = new ResourceMonitoringAgentImpl();

		resourceMonitorinAgent.initialize(config);
		gossipAgent.registerMessagePayloadAgent(resourceMonitorinAgent);
		gossipAgent.initialize(config);

		try (final Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.println("Enter 'p' for printing monitoring info. 'e' for exit!");
				String input = scanner.next();
				if (input.equalsIgnoreCase("p")) {
					printMonitoringData(gossipAgent, resourceMonitorinAgent);
				} else if (input.equalsIgnoreCase("e")) {
					System.exit(0);
				} else {
					System.out.println("Invalid input!");
				}
			}
		}
	}

	private void printMonitoringData(final GossipAgent gossipAgent,
			final ResourceMonitoringAgentImpl resourceMonitorinAgent) {
		final StringBuffer buffer = new StringBuffer();

		final InstancesInfo instancesInfo = gossipAgent.getCurrentInstancesInfo();
		final Set<InstanceId> liveInstances = instancesInfo.getLiveInstances();
		final Set<InstanceId> downInstances = instancesInfo.getDownInstances();

		buffer.append("\n{");
		buffer.append("\n\t\"live-instances\": \"");
		final String liveInstanceList = String.join(", ",
				liveInstances.parallelStream().map(instanceId -> instanceId.toString()).collect(Collectors.toList()));

		buffer.append(liveInstanceList + "\",");
		buffer.append("\n\t\"down-instances\": \"");
		final String downInstanceList = String.join(", ",
				downInstances.parallelStream().map(instanceId -> instanceId.toString()).collect(Collectors.toList()));

		buffer.append(downInstanceList + "\",");

		liveInstances.forEach(instanceId -> {
			final MonitoredData monitoredData = resourceMonitorinAgent.getPayloadDataForInstance(instanceId);

			buffer.append("\n\t{");
			buffer.append("\n\t\t\"instance-id\" : \"" + monitoredData.getInstanceId() + "\",");
			buffer.append("\n\t\t\"monitoring-data\" : {\n\t\t\t");

			String monitoringData = String.join(",\n\t\t\t",
					monitoredData.getMonitoredData().parallelStream()
							.map(data -> data.getResourceName() + "\" : \"" + data.getCurrentValue() + "\"")
							.collect(Collectors.toList()));
			buffer.append(monitoringData);

			buffer.append("\n\t\t}");
			buffer.append("\n\t}");
		});
		buffer.append("\n}");
		
		System.out.println(buffer.toString());
	}

}
