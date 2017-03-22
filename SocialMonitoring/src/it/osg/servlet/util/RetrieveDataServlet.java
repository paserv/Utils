package it.osg.servlet.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class RetrieveDataServlet extends HttpServlet {

	//private static String confTable = "pages";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {

		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		
		String op = req.getParameter("operazione");
		String tabella = req.getParameter("tabella");

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q;
		PreparedQuery pq;
		q = new Query(tabella);
		pq = datastore.prepare(q);
		
		if (op.equalsIgnoreCase("select")) {

			for (Entity res : pq.asIterable()) {
				out.println(res.getKey().toString() + ",");
				Map<String,Object> result = res.getProperties();
				Set<String> set = result.keySet();
				Iterator<String> iter = set.iterator();
				while(iter.hasNext()) {
					String currKey = iter.next();
					Object currProp = result.get(currKey);
					if (currProp instanceof Text) {
						out.println(((Text) currProp).getValue()  + ",");
					} else {
						out.println(result.get(currKey).toString() + ",");
					}	
				}
				out.println("<br>");
				
			}
		} else if (op.equalsIgnoreCase("delete")) {
			out.println("Tabella = " + tabella + "<br>Cancellata<br><br>");

			for (Entity res : pq.asIterable()) {
				datastore.delete(res.getKey());
			}
		}
		
		else {
			Filter idFilter = new FilterPredicate("idTransaction", FilterOperator.EQUAL, "bcc391996b23fe14e3527d84381d6992");
			Filter sindacoFilter = new FilterPredicate("pageId", FilterOperator.EQUAL, "56208847866");
			Filter compositeFilter = CompositeFilterOperator.and(idFilter, sindacoFilter);
			q = new Query(tabella).setFilter(compositeFilter);
			pq = datastore.prepare(q);
			for (Entity res : pq.asIterable()) {
				out.println(res.getKind().toString() + ",");
				Map<String,Object> result = res.getProperties();
				Set<String> set = result.keySet();
				Iterator<String> iter = set.iterator();
				while(iter.hasNext()) {
					String currKey = iter.next();
					out.println(result.get(currKey).toString() + ",");
				}
				out.println("<br>");
				
			}
		}



	}


//	private ArrayList<Object> getConfPage(String entityName, String campo) {
//		ArrayList<Object> result = new ArrayList<Object>();
//		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//		Query q = new Query(entityName);
//		PreparedQuery pq = datastore.prepare(q);
//
//		for (Entity res : pq.asIterable()) {
//			//String id = res.getKey().getName();
//			Object value = res.getProperty(campo);
//			result.add(value);
//		}
//		return result;
//
//	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		doGet(req, resp);
	}



}
