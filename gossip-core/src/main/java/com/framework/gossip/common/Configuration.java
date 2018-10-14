package com.framework.gossip.common;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.framework.gossip.domain.InstanceId;

public class Configuration {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private InstanceId instanceId;
	private String broadcastIP;
	private int basePort;
	private int localPort;
	private long tGossip;
	private int tCleanupCount;
	private int tPartitionCount;

	public Configuration() {
		super();
	}

	public final InstanceId getInstanceId() {
		return instanceId;
	}

	public final String getBroadcastIP() {
		return broadcastIP;
	}

	public final int getBasePort() {
		return basePort;
	}

	public final int getLocalPort() {
		return localPort;
	}

	public final long getGossipDelay() {
		return tGossip;
	}

	public final int getCleanupCount() {
		return tCleanupCount;
	}

	public final int getPartitionCount() {
		return tPartitionCount;
	}

	public final void setInstanceId(String providedInstanceId) {
		instanceId = new InstanceId(providedInstanceId);
	}

	public final void setBroadcastIP(String broadcastIP) {
		this.broadcastIP = broadcastIP;
	}

	public final void setBasePort(int basePort) {
		if(basePort <= 0) {
			throw new IllegalArgumentException("The Base Port must not be zero or negative!");
		}
		this.basePort = basePort;
	}

	public final void setLocalPort(int localPort) {
		if(localPort <= 0) {
			throw new IllegalArgumentException("The Local Port must not be zero or negative!");
		}
		
		this.localPort = localPort;
	}

	public final void setGossipDelay(long tGossip) {
		this.tGossip = tGossip;
	}

	public final void setCleanupCount(int tCleanupCount) {
		this.tCleanupCount = tCleanupCount;
	}

	public final void setPartitionCount(int tPartitionCount) {
		this.tPartitionCount = tPartitionCount;
	}
	
	public final void validateConfigComplete() {
		Objects.requireNonNull(instanceId, "Instance Id can not be null!");
		Objects.requireNonNull(broadcastIP, "Broadcast IP can not be null!");
		if(broadcastIP.isEmpty() || isInvalidIP()) {
			throw new IllegalArgumentException("Broadcast IP must be valid IP");
		}
		validateNumber("Cleanup Count", tCleanupCount, 1, Integer.MAX_VALUE);
		validateNumber("Gossip Delay", tGossip, 50, Integer.MAX_VALUE);
		validateNumber("Partition Count", tPartitionCount, 1, Integer.MAX_VALUE);
		
		if(basePort == 0) {
			logger.info("Base Port value not set. Using the default i.e. "+Constants.DEFAULT_PORT_FOR_SERVICE);
			basePort = Constants.DEFAULT_PORT_FOR_SERVICE;
		} else {
			validateNumber("Base Port", basePort, 1024, 65535 - Constants.MAX_INSTANCES_PER_HOST);
		}
		
		if(localPort == 0) {
			logger.info("Local Port value not set. Using the default i.e. "+Constants.DEFAULT_PORT_FOR_SERVICE);
			localPort = Constants.DEFAULT_PORT_FOR_SERVICE;
		} else {
			validateNumber("Local Port", localPort, basePort, basePort+Constants.MAX_INSTANCES_PER_HOST);
		}
	}
	
	private void validateNumber( final String paramName, final long number, final long lowerLimit, final long upperLimit) {
		if(number < lowerLimit || number > upperLimit) {
			throw new IllegalArgumentException("Invalid value for "+paramName+". Must be between "+lowerLimit+" and "+upperLimit);
		}
	}

	private boolean isInvalidIP() {
		final String[] IPParts = broadcastIP.split("\\.");
		if(IPParts.length != 4) {
			return true;
		}
		
		for(String part : IPParts) {
			try {
				int partAsInt = Integer.parseInt(part);
				validateNumber("IP Part", partAsInt, 0, 255);
			} catch(NumberFormatException nfe) {
				return true;
			} catch(IllegalArgumentException iae) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return "Configuration : [{instanceId: " + instanceId + "}, {broadcastIP: " + broadcastIP + "}, {basePort: "
				+ basePort + "}, {localPort: " + localPort + "}, {Gossip Delay(ms): " + tGossip + "}, {Cleanup Count: "
				+ tCleanupCount + "}, {Partition Count: " + tPartitionCount + "}]";
	}

}
