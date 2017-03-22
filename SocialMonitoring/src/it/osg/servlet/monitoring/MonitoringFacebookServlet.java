package it.osg.servlet.monitoring;

import it.osg.utils.Constants;
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

@SuppressWarnings("serial")
public class MonitoringFacebookServlet extends HttpServlet {

	

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String idPage = req.getParameter("idPage");
		String monitoringTable = req.getParameter("monitoringTable");

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		String jsonString = JSONObjectUtil.retrieveJson(Constants.FACEBOOK_GRAPH_API_ROOT_URL + idPage);
		ArrayList<Hashtable<String, Object>> analisi = FacebookUtils.likeTalkAnalysis(jsonString);

		Iterator<Hashtable<String, Object>> iterAna = analisi.iterator();
		while (iterAna.hasNext()) {
			Entity currEntity = new Entity(monitoringTable);
			currEntity.setProperty("idFacebook", idPage);
			Hashtable<String, Object> currRow = iterAna.next();
			Enumeration<String> enumer = currRow.keys();
			while (enumer.hasMoreElements()) {
				String currKey = enumer.nextElement();
				Object currValue = currRow.get(currKey);
				currEntity.setProperty(currKey, currValue);
			}
			datastore.put(currEntity);

		}


	}


	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		doGet(req, resp);
	}

}
