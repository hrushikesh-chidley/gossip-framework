package com.framework.gossip;

import java.io.Serializable;

import com.framework.gossip.domain.InstanceId;

public interface GossipMessagePayloadAgent<Payload extends Serializable> {

	Payload getPayloadDataForInstance(InstanceId instanceId);

	void submitReceivedPayloadData(Serializable payloadData);

}
