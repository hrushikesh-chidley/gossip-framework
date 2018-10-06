package com.monitoring.extension.tests;

import com.framework.gossip.error.GossipException;

public class SampleMicroservice1 extends AbstractMicroserviceTest {
	
	public static void main(String [] argv) throws GossipException {
		SampleMicroservice1 microservice1 = new SampleMicroservice1();
		microservice1.startMicroservice("1", 39875);
	}
}
