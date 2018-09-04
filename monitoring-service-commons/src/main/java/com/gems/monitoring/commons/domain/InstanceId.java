package com.gems.monitoring.commons.domain;

public class InstanceId {

	private String instanceId;
	
	public InstanceId( final String instanceId ) {
		this.instanceId = instanceId;
	}

	public final String getInstanceId() {
		return instanceId;
	}

	public final void setInstanceId( final String instanceId ) {
		this.instanceId = instanceId;
	}
	
	@Override
	public String toString() {
		return instanceId;
	}
	
	
}
