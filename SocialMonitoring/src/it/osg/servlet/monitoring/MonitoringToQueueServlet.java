package it.osg.servlet.monitoring;

import it.osg.utils.Constants;
import it.osg.utils.DatastoreUtils;
import it.osg.utils.FacebookUtils;
import it.osg.utils.JSONObjectUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public abstract class MonitoringToQueueServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public abstract String getMonitorEntityId();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getMonitorConf(getMonitorEntityId());
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		doGet(req, resp);
	}
	
	public void getMonitorConf(String idmonitorentity) throws IOException {
				
		String monitoringQueue = "";
		String monitoringIdTable = "";
		String monitoringIdFieldInTable = "";
		String monitoringServlet = "";
		String monitoringTable = "";
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q;
		PreparedQuery pq;
		q = new Query(Constants.CONF_TABLE);
		pq = datastore.prepare(q);
		for (Entity res : pq.asIterable()) {
			if (((String) res.getProperty("idmonitorentity")).equalsIgnoreCase(idmonitorentity)) {
				monitoringQueue = (String) res.getProperty("monitoringqueue");
				monitoringIdTable = (String) res.getProperty("monitoringidtable");
				monitoringIdFieldInTable = (String) res.getProperty("monitoringidfieldintable");
				monitoringServlet = (String) res.getProperty("monitoringservlet");
				monitoringTable = (String) res.getProperty("monitoringtable");
				
				Queue queue = QueueFactory.getQueue(monitoringQueue);
				ArrayList<String> ids = DatastoreUtils.getPropertyList(monitoringIdTable, monitoringIdFieldInTable);
				Iterator<String> iter = ids.iterator();
				while (iter.hasNext()) {
					String idPage = iter.next();
					queue.add(TaskOptions.Builder.withUrl("/" + monitoringServlet).param("idPage", idPage).param("monitoringTable", monitoringTable));			
				}
			}
			
		}
	}
}
