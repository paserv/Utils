package it.osg.servlet.monitoring.facebook;

import it.osg.servlet.monitoring.MonitoringToQueueServlet;

public class MonitoringSindacoFacebookServletQueue extends MonitoringToQueueServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMonitorEntityId() {
		return "facebookmonitorsindaci";
	}

	
}
