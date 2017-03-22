package it.osg.servlet;

import it.osg.utils.DatastoreUtils;
import it.osg.utils.DateUtils;
import it.osg.utils.Utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public abstract class SplitTaskServlet extends HttpServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract int getStep();
	public abstract String getSubtask();
	public abstract String getJointask();
	public abstract String getQueueName();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {

		String strCallResult = "";
		resp.setContentType("text/plain");

		ArrayList<String> pages = new ArrayList<String>();

		//INPUT DATA
		String pageId = req.getParameter("pageId");
		String IDField = req.getParameter("IDField");
		String from = req.getParameter("from");
		String to = req.getParameter("to");	
		String mail = req.getParameter("mail");
		String tabAnagrafica = req.getParameter("tabAnagrafica");
		try  {
			if (pageId == null || from == null || to == null || mail == null) throw new Exception("Fields cannot be empty.");
			if (mail.length() == 0) throw new Exception("To field cannot be empty.");
			strCallResult = "Successfully created a Task in the Queue.\nYou will receive an email as data are available";
			resp.getWriter().println(strCallResult);

			String timestamp = String.valueOf(System.currentTimeMillis());
			String idTransaction = Utils.MD5(pageId + mail + timestamp);

			if (!Utils.isDouble(pageId)) {
				pages = DatastoreUtils.getPropertyList(pageId, IDField);
			} else {
				pages.add(pageId);
			}

			//GET TO DATE
			String toDay = to.substring(0, 10) + " 23:59:59";
			Date t = null;
			try {
				t = DateUtils.parseDateAndTime(toDay);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			Queue queue = QueueFactory.getQueue(getQueueName());

			int numTask = 0;

			Iterator<String> iterPages = pages.iterator();
			while (iterPages.hasNext()) {
				String currPageId = iterPages.next();
				//SPLITTO N GIORNI ALLA VOLTA ED INVIO IN CODA
				String from1 = from.substring(0, 10) + " 00:00:00";
				Date f1 = DateUtils.parseDateAndTime(from1);
				while (true) {
//					String to1 = DateUtils.formatDateAndTime(DateUtils.addDayToDate(DateUtils.parseDateAndTime(from1), getStep()));
					String to1 = DateUtils.formatDateAndTime(DateUtils.addHoursToDate(DateUtils.parseDateAndTime(from1), getStep()));
					Date t1 = DateUtils.parseDateAndTime(to1);
					if (DateUtils.compareDate(t, t1) >= 0) {
						queue.add(TaskOptions.Builder.withUrl("/" + getSubtask()).param("idTransaction", idTransaction).param("from", from1).param("to", to1).param("pageId", currPageId));
						numTask = numTask + 1;
						f1 = t1;
						from1 = DateUtils.formatDateAndTime(f1);
					} else {
						queue.add(TaskOptions.Builder.withUrl("/" + getSubtask()).param("idTransaction", idTransaction).param("from", from1).param("to", toDay).param("pageId", currPageId));
						numTask = numTask + 1;
						break;
					}
				}	
			}

			//TASK CHE MONITORA GLI ALTRI TASK (JOINTASKSERVLET)
			queue.add(TaskOptions.Builder.withUrl("/" + getJointask()).param("idTransaction", idTransaction).param("numTask", String.valueOf(numTask)).param("from", from).param("to", to).param("pageId", pageId).param("IDField", IDField).param("mail", mail).param("timestamp", timestamp).param("tabAnagrafica", tabAnagrafica));	

		} catch (Exception ex) {
			strCallResult = "Fail: " + ex.getMessage();
			resp.getWriter().println(strCallResult);
		}

	}


	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}



}
