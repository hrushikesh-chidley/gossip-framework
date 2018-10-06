package com.monitoring.extension.tests;

import com.framework.gossip.error.GossipException;

public class SampleMicroservice3 extends AbstractMicroserviceTest {
	
	public static void main(String [] argv) throws GossipException {
		SampleMicroservice3 microservice3 = new SampleMicroservice3();
		microservice3.startMicroservice("3", 39877);
	}
}
