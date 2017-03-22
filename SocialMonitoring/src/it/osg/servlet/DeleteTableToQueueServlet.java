package it.osg.servlet;

import it.osg.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class DeleteTableToQueueServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		ArrayList<String> tablesToDelete = new ArrayList<String>();
		String queueName = "";
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q;
		PreparedQuery pq;
		q = new Query(Constants.SETTINGS_TABLE);
		pq = datastore.prepare(q);
		for (Entity res : pq.asIterable()) {
			if (((String) res.getProperty("property")).equalsIgnoreCase("deletetable")) {
				tablesToDelete.add((String) res.getProperty("value"));
			}
			if (((String) res.getProperty("property")).equalsIgnoreCase("deletetablequeue")) {
				queueName = (String) res.getProperty("value");
			}
		}
		
		for (int i = 0; i < tablesToDelete.size(); i++) {
			String currTable = tablesToDelete.get(i);
			Queue queue = QueueFactory.getQueue(queueName);
			queue.add(TaskOptions.Builder.withUrl("/deletetable").param("tablename", currTable));
		}
		
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
	
	
}
