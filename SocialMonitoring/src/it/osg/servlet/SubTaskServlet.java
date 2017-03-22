package it.osg.servlet;

import it.osg.utils.DateUtils;
import it.osg.utils.ShardedCounter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class SubTaskServlet extends HttpServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected abstract void runSubTask(String idTranscation, String pageId, Date from, Date to);
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {

		//INPUT DATA
		String idTransaction = req.getParameter("idTransaction");
		String pageId = req.getParameter("pageId");
		String f = req.getParameter("from");
		String t = req.getParameter("to");

		//GET DATE
		Date from = null;
		Date to = null;
		try {
			from = DateUtils.parseDateAndTime(f);
			to = DateUtils.parseDateAndTime(t);
		} catch (ParseException e) {
			e.printStackTrace();
		}
				
		runSubTask(idTransaction, pageId, from, to);
		
		//SAVE DATA TO DATASTORE AND INCREMENT 1 TASK
//		ShardedCounter counter = new ShardedCounter();
//		counter.increment(idTransaction);
		
			
	}


	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}



}
