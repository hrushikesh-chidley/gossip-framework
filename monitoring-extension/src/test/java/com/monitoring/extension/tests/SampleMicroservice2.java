package com.monitoring.extension.tests;

import com.framework.gossip.error.GossipException;

public class SampleMicroservice2 extends AbstractMicroserviceTest {
	
	public static void main(String [] argv) throws GossipException {
		SampleMicroservice2 microservice2 = new SampleMicroservice2();
		microservice2.startMicroservice("2", 39876);
	}
}
