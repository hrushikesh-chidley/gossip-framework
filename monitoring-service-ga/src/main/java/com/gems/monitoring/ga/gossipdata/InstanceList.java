package com.gems.monitoring.ga.gossipdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class InstanceList {

	private List<Instance> instanceList;
	
	public InstanceList() {
		super();
		instanceList = new ArrayList<>(500);
	}
	
	public final void addInstanceToList( final Instance instance ) {
		instanceList.add(instance);
	}
	
	public final boolean isInstanceKnown( final Instance instance ) {
		return instanceList.contains(instance);
	}
	
	public final List<Instance> getAllInstances() {
		return Collections.unmodifiableList(instanceList);
	}
	
}
