package com.gems.monitoring.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class MonitoredData implements Serializable {

	/** Serial Version Id */
	private static final long serialVersionUID = 2592654743068505173L;

	private InstanceId instanceId;
	
	private Set<ResourceData<?>> monitoredDataSet = new HashSet<>();

	public final InstanceId getInstanceId() {
		return instanceId;
	}

	public final void setInstanceId(final InstanceId instanceId) {
		this.instanceId = instanceId;
	}

	public final Set<ResourceData<?>> getMonitoredData() {
		return monitoredDataSet;
	}

	public final void addToMonitoredData(final ResourceData<?> monitoredData ) {
		monitoredDataSet.add(monitoredData);
	}
	
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("[{Instance Id: "+instanceId+"}, ");
		monitoredDataSet.forEach(data -> buffer.append(data.toString()+","));
		return buffer.substring(0, buffer.length() - 1)+"]";
	}
}
