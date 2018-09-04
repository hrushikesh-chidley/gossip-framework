package com.gems.monitoring.commons.util;

import com.gems.monitoring.commons.domain.MonitoredData;

public class PrettyPrinter {
	
	public static final String printMonitoringData( final MonitoredData monitoringData ) {
		final StringBuilder builder = new StringBuilder();
		builder.append("\n{");
		builder.append("\n\t\"instance-id\" : \""+monitoringData.getInstanceId()+"\",");
		builder.append("\n\t\"monitoring-data\" : {");
		monitoringData.getMonitoredData().parallelStream().forEach( data -> 
		{	
			builder.append("\n\t\t\""+data.getResourceName()+"\" : \""+data.getCurrentValue()+"\"");
		});
		builder.append("\n\t}");
		builder.append("\n}");
		return builder.toString();
	}

}
