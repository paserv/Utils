package it.osg.servlet;

import it.osg.utils.Constants;
import it.osg.utils.MailUtils;
import it.osg.utils.ShardedCounter;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public abstract class JoinTaskServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected abstract long getTimeout();
	protected abstract long getDelay();
	
	protected abstract String getSubjectMail(String pageId);
	protected abstract String getBodyMail(String from, String to, String timestamp, String pageId, long elapsedTime);
	protected abstract String getAttachFileName(String pageId);
	protected abstract String getAttachFile(String idTransaction, String from, String to, String tabAnag);
	protected abstract String getJoinTaskName();
	public abstract String getQueueName();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {

		//INPUT DATA
		String numTask = req.getParameter("numTask");
		String idTransaction = req.getParameter("idTransaction");
		String from = req.getParameter("from");
		String to = req.getParameter("to");
		String mail = req.getParameter("mail");
		String timestamp = req.getParameter("timestamp");
		String pageId = req.getParameter("pageId");
		String IDField = req.getParameter("IDField");
		String tabAnagrafica = req.getParameter("tabAnagrafica");
		
		long elapsedTime = (System.currentTimeMillis() - Long.valueOf(timestamp))/1000;

		if (isTransactionEnded(idTransaction, numTask)) {

			MailUtils.sendMail(mail, getSubjectMail(pageId), getBodyMail(from, to, timestamp, pageId, elapsedTime), getAttachFileName(pageId), getAttachFile(idTransaction, from, to, tabAnagrafica));
//			ShardedCounter counter = new ShardedCounter();
//			counter.delete(idTransaction);

		} else {
			if (getTimeout() - elapsedTime > 0) {
				//RIMETTE IN CODA SE STESSO DOPO UN CERTO DELAY
				try {
					Thread.sleep(getDelay());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//TASK CHE MONITORA GLI ALTRI TASK (JOINTASKSERVLET)
				Queue queue = QueueFactory.getQueue(getQueueName());
				queue.add(TaskOptions.Builder.withUrl("/" + getJoinTaskName()).param("numTask", numTask).param("idTransaction", idTransaction).param("from", from).param("to", to).param("mail", mail).param("timestamp", timestamp).param("pageId", pageId).param("IDField", IDField).param("tabAnagrafica", tabAnagrafica));	
			} else {
				//TODO SALVA IL SALVABILE ED INVIA LA MAIL
			}
		}

	}


	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}

	private boolean isTransactionEnded (String idTransaction, String numTask) {
//		ShardedCounter counter = new ShardedCounter();
//		long totalTask = Long.valueOf(numTask);
//		long executedTask = counter.getCount(idTransaction);
//		return totalTask == executedTask;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q;
		PreparedQuery pq;
		Filter idFilter = new FilterPredicate(Constants.ID_TRANSACTION_FIELD, FilterOperator.EQUAL, idTransaction);
		q = new Query(Constants.TASK_TABLE).setFilter(idFilter);
		pq = datastore.prepare(q);
		int executedTask = pq.countEntities();
		if (Integer.valueOf(numTask) == executedTask) return true;
		else return false;
	}


}
