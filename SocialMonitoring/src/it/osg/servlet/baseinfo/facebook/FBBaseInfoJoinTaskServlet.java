package it.osg.servlet.baseinfo.facebook;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import it.osg.servlet.JoinTaskServlet;
import it.osg.utils.Constants;
import it.osg.utils.DatastoreUtils;
import it.osg.utils.DateUtils;

public class FBBaseInfoJoinTaskServlet extends JoinTaskServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected long getTimeout() {
		return (Long.valueOf((String) DatastoreUtils.getValue(Constants.SETTINGS_TABLE, "property", "jointimeout", "value")));
	}

	@Override
	protected long getDelay() {
		return Long.valueOf((String) DatastoreUtils.getValue(Constants.SETTINGS_TABLE, "property", "joindelay", "value"));
	}

	@Override
	protected String getSubjectMail(String pageId) {
		return "BASE INFO relativi alla pagina Facebook con ID " + pageId;
	}

	@Override
	protected String getBodyMail(String from, String to, String timestamp, String pageId, long elapsedTime) {
		return "Periodo di riferimento:\nFROM: " + from + "\nTO: " + to + "\nQuery iniziata il: " + DateUtils.parseTimestamp(Long.valueOf(timestamp)) + "\nElapsed Time: " + elapsedTime + "\n\nRisultati per ID = " + pageId;
	}

	@Override
	protected String getAttachFileName(String pageId) {
		return pageId + ".csv";
	}

	@Override
	protected String getAttachFile(String idTransaction, String from, String to, String tabAnag) {
		String result = "ID;BaseInfoDateStart;TotalFanStart;TalkingAboutStart;BaseInfoDateEnd;TotalFanEnd;TalkingAboutEnd;LastPost\n";
//		String result = "ID;BaseInfoDate;TotalFan;TalkingAbout;LastPost\n";
		DatastoreService DS = DatastoreServiceFactory.getDatastoreService();
		Query q;
		PreparedQuery pq;
		Filter idFilter = new FilterPredicate("idTransaction", FilterOperator.EQUAL, idTransaction);
		q = new Query(Constants.TASK_TABLE).setFilter(idFilter);
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			result = result + DatastoreUtils.getProperty(ent, "pageId") + ";" + DatastoreUtils.getProperty(ent, "baseInfoDateStart") + ";"  + DatastoreUtils.getProperty(ent, "fanCountStart") + ";" + DatastoreUtils.getProperty(ent, "talkingAboutCountStart") + ";" + DatastoreUtils.getProperty(ent, "baseInfoDate") + ";" + DatastoreUtils.getProperty(ent, "fanCount") + ";" + DatastoreUtils.getProperty(ent, "talkingAboutCount") + ";" + DatastoreUtils.getProperty(ent, "endDate") + "\n";
//			result = result + ent.getProperty("pageId") + ";" + ent.getProperty("baseInfoDate") + ";" + ent.getProperty("fanCount") + ";" + ent.getProperty("talkingAboutCount") + ";" + ent.getProperty("endDate") + "\n";
		}
		
		return result;
	}

	@Override
	protected String getJoinTaskName() {
		return "FBbaseinfojointask";
	}

	@Override
	public String getQueueName() {
		return "baseinfo";
	}

}
