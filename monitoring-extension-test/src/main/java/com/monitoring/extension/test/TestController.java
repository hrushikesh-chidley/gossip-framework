package com.monitoring.extension.test;


import java.util.Set;
import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.framework.gossip.GossipAgent;
import com.framework.gossip.GossipMessagePayloadAgent;
import com.framework.gossip.domain.InstancesInfo;
import com.framework.gossip.error.GossipException;
import com.monitoring.extension.domain.MonitoredData;
import com.framework.gossip.common.Configuration;


@RestController
public class TestController {
	
	@Resource
	private GossipAgent gossipAgent;
	
	@Resource
	private GossipMessagePayloadAgent monitoringAgent;
	
	@Resource(name="gossipConfig")
	private Configuration config;
	
	@Resource
	private TestConfiguration testConfiguration;
	
	
	@PostConstruct
	public void init() throws GossipException {
		config.setInstanceId(testConfiguration.getInstanceId());
		config.setBroadcastIP(testConfiguration.getBroadcastIP());
		config.setLocalPort(testConfiguration.getLocalPort());
		config.setGossipDelay(testConfiguration.getGossipDelay());
		config.setCleanupCount(testConfiguration.getCleanupCount());
		config.setPartitionCount(testConfiguration.getPartitionCount());
		
		
		monitoringAgent.initialize(config);
		gossipAgent.registerMessagePayloadAgent(monitoringAgent);
		gossipAgent.initialize(config);
	}

    @RequestMapping("/md")
    public HealthResponse index() {
    	final HealthResponse response = new HealthResponse();
    	
    	final InstancesInfo info = gossipAgent.getCurrentInstancesInfo();
    	response.info = info;
    	info.getLiveInstances().forEach(instanceId -> {
			final MonitoredData monitoredData = (MonitoredData)monitoringAgent.getPayloadDataForInstance(instanceId);
			response.data.add(monitoredData);
    	});
        return response;
    }
    
    private class HealthResponse {
    	private InstancesInfo info;
    	private Set<MonitoredData> data = new HashSet<>();
		public final InstancesInfo getInfo() {
			return info;
		}
		public final Set<MonitoredData> getData() {
			return data;
		}
    	
    }

}