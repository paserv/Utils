package it.osg.servlet.util;

import it.osg.utils.Constants;
import it.osg.utils.DatastoreUtils;
import it.osg.utils.DateUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

public class DeltaFanServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {

		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();

		String tabAnagrafica = req.getParameter("tabAnagrafica");
		String IDField = req.getParameter("IDField");
		String from = req.getParameter("from");
		String to = req.getParameter("to");	
		String to2 = to.substring(0, 10) + " 23:59:59";
		out.println("ID;StartFan;EndFan<br>");

		ArrayList<String> pages = new ArrayList<String>();

		pages = DatastoreUtils.getPropertyList(tabAnagrafica, IDField);

		Iterator<String> iterPages = pages.iterator();
		while (iterPages.hasNext()) {
			String currPageId = iterPages.next();
			Hashtable<String, Object> toHash = getFanCount(currPageId, to2, FilterOperator.LESS_THAN_OR_EQUAL, SortDirection.DESCENDING);
			Hashtable<String, Object> fromHash = getFanCount(currPageId, from, FilterOperator.GREATER_THAN_OR_EQUAL, SortDirection.ASCENDING); 
			long startingFan = (Long) fromHash.get("like_count");
			long endingFan = (Long) toHash.get("like_count");
			out.println(currPageId + ";" + startingFan + ";" + endingFan + "<br>");
		}

	}


	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}

	private Hashtable<String, Object> getFanCount (String idPage, String date, FilterOperator fo, SortDirection sd) {

		Hashtable<String, Object> result = new Hashtable<String, Object>();
		result.put("like_count", 0L);
		try {
			result.put("date", DateUtils.parseDate("01-01-1970 00:00:00"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q;
		try {
			Filter fromFilter = new FilterPredicate("date", fo, DateUtils.parseDateAndTime(date));
			Filter idPageFilter = new FilterPredicate("idFacebook", FilterOperator.EQUAL, idPage);
			Filter compositeFilter = CompositeFilterOperator.and(idPageFilter, fromFilter);
			q = new Query(Constants.FACEBOOK_MONITOR_TABLE).setFilter(compositeFilter).addSort("date", sd);
			PreparedQuery pq = datastore.prepare(q);
			for (Entity ent : pq.asIterable()) {
				if ( ent.getProperty("like_count") != null && !((Long) ent.getProperty("like_count")).equals(0L)) {
					result.put("like_count", (Long) ent.getProperty("like_count"));
					result.put("date", (Date) ent.getProperty("date"));
					return result;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

}
