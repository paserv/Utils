package it.osg.servlet.monitoring;

import it.osg.utils.YouTubeUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class MonitoringYouTubeServlet extends HttpServlet {



	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String idPage = req.getParameter("idPage");
		String monitoringTable = req.getParameter("monitoringTable");

		Entity currEntity = new Entity(monitoringTable);
		
		currEntity.setProperty("idYoutube", idPage);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();		
		Hashtable<String, String> baseInfo = YouTubeUtils.getBaseInfo(idPage);
		currEntity.setProperty("subscribers", baseInfo.get("subscribers"));
		currEntity.setProperty("views", baseInfo.get("views"));
		
		Calendar cal= Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(Calendar.HOUR_OF_DAY, 2);
		SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date cestDate= cal.getTime();

		currEntity.setProperty("timestamp", cal.getTimeInMillis());
		currEntity.setProperty("date", cestDate);
		
		datastore.put(currEntity);

	}




	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		doGet(req, resp);
	}

}
