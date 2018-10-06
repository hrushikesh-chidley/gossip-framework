package com.gems.monitoring.function;

import java.io.Serializable;

import com.gems.monitoring.domain.InstanceId;

public interface GossipMessagePayloadAgent<Payload extends Serializable> {

	Payload getPayloadDataForInstance(InstanceId instanceId);

	void submitReceivedPayloadData(Serializable payloadData);

}
