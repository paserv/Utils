package it.osg.servlet.monitoring.youtube;

import it.osg.servlet.monitoring.MonitoringToQueueServlet;

public class MonitoringSindacoYouTubeServletQueue extends MonitoringToQueueServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMonitorEntityId() {
		return "youtubemonitorsindaci";
	}

	
}
