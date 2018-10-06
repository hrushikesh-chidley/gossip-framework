package com.gems.monitoring.commons.domain;

import java.io.Serializable;

public class InstanceId implements Serializable {

	private static final long serialVersionUID = 698321960508784197L;

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
	
	@Override
	public boolean equals(Object anotherInstanceId) {
		return anotherInstanceId instanceof InstanceId 
				&& ((InstanceId) anotherInstanceId).getInstanceId().equals(instanceId);
	}

	@Override
	public int hashCode() {
		return instanceId.hashCode();
	}
	
	
}
