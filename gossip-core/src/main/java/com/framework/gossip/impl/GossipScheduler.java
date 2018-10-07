package com.framework.gossip.impl;

import java.util.Optional;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.framework.gossip.GossipAgent;
import com.framework.gossip.domain.Instance;
import com.framework.gossip.error.GossipException;

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
		} catch(GossipException | RuntimeException e) {
			logger.error("The scheduled gossip round failed with exception. Details : "+e.getMessage());
			logger.error("Exception Trace", e);
		}
	}
	
	/**
	 * This method performs the scheduled execution of gossip algorithm as explained in below steps
	 * <ul>
	 * <li>For each entry in ‘Gossip Map’, increment the value by one (1), except for entry of its own instance id</li>
	 * <li>For every entry in ‘Gossip Map’ where entry value multiplied by Tgossip is greater than Tclenaup, 
	 * update the corresponding entry of same key in the ‘Suspect Map’ with value set to ‘TRUE’</li> 
	 * <li>If ‘Suspect Map’ is updated, modify the ‘Suspect Matrix’ with new value of ‘Suspect Map’</li> 
	 * <li>If local ‘Instance List’ is empty, then check if it has been Tcleanup time since last broadcast message. 
	 * If not, then ignore the trigger and wait. If yes, then re-broadcast the initial identifier broadcast message</li>
	 * <li>If the list is not empty, then pick a live element i.e. instance whose value in ‘Live Map’ is true, from 
	 * ‘Instance List’ randomly</li>
	 * <li>Send this picked instance this Instance’s own id, IP and port, its Gossip Map, its Suspect Matrix  as a 
	 * single gossip message</li>
	 * </ul>
	 * 
	 * @throws GossipException in case of any error during handling of this trigger. It should be noted that this exception only
	 * impacts this execution of the trigger. The next execution re-perfoms all steps again irrespective of any exception in earlier 
	 * execution
	 */
	private void performScheduledGossip() throws GossipException {
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
