package com.monitoring.extension.tests;

import com.framework.gossip.error.GossipException;

public class SampleMicroservice4 extends AbstractMicroserviceTest {
	
	public static void main(String [] argv) throws GossipException {
		SampleMicroservice4 microservice4 = new SampleMicroservice4();
		microservice4.startMicroservice("4", 39878);
	}
}
