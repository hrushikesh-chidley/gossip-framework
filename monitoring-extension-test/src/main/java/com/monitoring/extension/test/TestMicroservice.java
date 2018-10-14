package com.framework.gossip.extension.monitoring.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Bean;

import com.framework.gossip.GossipAgent;
import com.framework.gossip.impl.GossipAgentImpl;
import com.framework.gossip.GossipMessagePayloadAgent;
import com.monitoring.extension.ResourceMonitoringAgentImpl;
import com.framework.gossip.common.Configuration;


@SpringBootApplication
@ComponentScan({"com.monitoring", "com.framework"})
public class TestMicroservice {

	public static void main(String [] argv) {
		SpringApplication.run(TestMicroservice.class, argv);
	}
	
	@Bean
    public GossipAgent gossipAgent() {
        return new GossipAgentImpl(); 
    }
	
	
	@Bean
    public GossipMessagePayloadAgent monitoringAgent() {
        return new ResourceMonitoringAgentImpl(); 
    }
	
	@Bean
    public Configuration gossipConfig() {
        return new Configuration(); 
    }
	
	
}
