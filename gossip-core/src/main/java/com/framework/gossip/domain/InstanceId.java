package com.framework.gossip.domain;

import java.io.Serializable;

public class InstanceId implements Serializable {

	private static final long serialVersionUID = 698321960508784197L;

	private String instanceId;
	
	public InstanceId( final String instanceId ) {
		this.instanceId = validateId(instanceId);
	}

	public final String getInstanceId() {
		return instanceId;
	}

	public final void setInstanceId( final String instanceId ) {
		this.instanceId = validateId(instanceId);
	}
	
	private String validateId( final String instanceId ) {
		if(instanceId == null || instanceId.isEmpty()) {
			throw new IllegalArgumentException("Instance Id can not be null or empty!");
		}
		return instanceId;
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
