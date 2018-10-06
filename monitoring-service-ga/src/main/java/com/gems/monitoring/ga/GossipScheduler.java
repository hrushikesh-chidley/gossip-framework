package com.gems.monitoring.ga;

import java.util.Optional;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gems.monitoring.domain.Instance;
import com.gems.monitoring.error.MonitoringServiceException;
import com.gems.monitoring.function.GossipAgent;

public class GossipScheduler extends TimerTask {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final GossipAgent gossipAgent;
	
	public GossipScheduler( final GossipAgent gossipAgent ) {
		this.gossipAgent = gossipAgent;
	}

	@Override
	public void run() {
		try {
			performScheduledGossip();
		} catch(MonitoringServiceException | RuntimeException e) {
			logger.error("The scheduled gossip round failed with exception. Details : "+e.getMessage());
			logger.error("Exception Trace", e);
		}
	}
	
	private void performScheduledGossip() throws MonitoringServiceException {
		logger.debug("Running scheduled gossip!!");
		final GossipData gossipData = GossipData.getInstance();
		gossipData.updateForScheduledGossipTrigger();
		
		final Optional<Instance> randomLiveInstance = gossipData.getRandomLiveInstanceIfAvailable();
		
		if(randomLiveInstance.isPresent()) {
			logger.debug("Neighbors are available for gossiping");
			logger.debug("Gossip intended with "+randomLiveInstance.get());
			gossipAgent.gossipWithNeighbor(randomLiveInstance.get());
		} else {
			logger.debug("Neighbors are not available for gossiping. Checking for broadcast!!");
			gossipAgent.broadcastIdMessageIfAppropriate();
		}
	}

}
