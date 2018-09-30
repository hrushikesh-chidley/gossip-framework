package com.gems.monitoring.ga.gossipdata;

import java.util.TimerTask;

import com.gems.monitoring.error.MonitoringServiceException;
import com.gems.monitoring.function.GossipAgent;

public class GossipScheduler extends TimerTask {
	
	private final GossipAgent gossipAgent;
	
	public GossipScheduler( final GossipAgent gossipAgent ) {
		this.gossipAgent = gossipAgent;
	}

	@Override
	public void run() {
		try {
			performScheduledGossip();
		} catch(MonitoringServiceException e) {
			e.printStackTrace();
		}
	}
	
	private void performScheduledGossip() throws MonitoringServiceException {
		GossipData gossipData = GossipData.getInstance();
		gossipData.updateForScheduledGossipTrigger();
		if(gossipData.AreInstancesAvailable()) {
			gossipAgent.gossipWithNeighbor(gossipData.getRandomLiveInstance());
		} else {
			gossipAgent.broadcastIdMessageIfAppropriate();
		}
	}

}
