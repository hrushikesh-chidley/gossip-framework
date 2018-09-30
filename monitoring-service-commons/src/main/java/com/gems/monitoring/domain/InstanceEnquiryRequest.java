package com.gems.monitoring.domain;

import com.gems.monitoring.net.NetworkAddress;

public final class InstanceEnquiryRequest extends Message {

	private static final long serialVersionUID = 5853271210705756369L;

	public InstanceEnquiryRequest(final NetworkAddress sourceAddress) {
		super(MessageTypes.INSTANCE_LIST_ENQUIRY_REQUEST, sourceAddress);
	}

	
}
