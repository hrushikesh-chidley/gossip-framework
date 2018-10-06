package com.gems.monitoring.message;

import java.util.List;

import com.gems.monitoring.domain.Instance;
import com.gems.monitoring.domain.NetworkAddress;

public final class InstanceEnquiryResponse extends Message {
	
	private static final long serialVersionUID = 6371887972204863113L;

	private final List<Instance> instanceList;

	public InstanceEnquiryResponse(final NetworkAddress sourceAddress, final List<Instance> instances) {
		super(MessageTypes.INSTANCE_LIST_ENQUIRY_RESPONSE, sourceAddress);
		this.instanceList = instances;
	}
	
	public final List<Instance> getReceivedInstanceList() {
		return instanceList;
	}

	
}
