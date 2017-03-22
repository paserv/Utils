package it.osg.servlet.psar.plus;

import it.osg.model.PSARData;
import it.osg.servlet.JoinTaskServlet;
import it.osg.utils.ArrayUtils;
import it.osg.utils.Constants;
import it.osg.utils.DatastoreUtils;
import it.osg.utils.DateUtils;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class PLJoinTaskServlet extends JoinTaskServlet {

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
		return "Dati relativi alla pagina Google Plus con ID " + pageId;
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
	protected String getJoinTaskName() {
		return "PLpsarjointask";
	}


	@Override
	public String getQueueName() {
		return "default";
	}

	@Override
	protected String getAttachFile(String idTransaction, String from, String to, String tabAnag) {


		String dataCSV = "Nome;ID Plus;Totale Activities;Totale Comments;Unique Authors;Totale Plus;Totale Shares\n";
		double numGiorni = 0;
		try {
			numGiorni = DateUtils.giorniTraDueDate(DateUtils.parseDateAndTime(from), DateUtils.parseDateAndTime(to));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		ArrayList<PSARData> psarData = DatastoreUtils.getPsarDataPL(Constants.TASK_TABLE, idTransaction);

		Hashtable<String, PSARData> joinedData = aggregatePsarData(psarData);

		Enumeration<String> keys = joinedData.keys();
		while (keys.hasMoreElements()) {
			String currKey = keys.nextElement();
			PSARData currPsar = joinedData.get(currKey);

			//DATI RICAVATI
//			double mediaPostFromPage = currPsar.postFromPageCount/numGiorni;
//			double commentsPerPost = currPsar.commentsCount/currPsar.postFromPageCount;
			double uniqueAuthors = ArrayUtils.removeDuplicate(currPsar.authors).size();
//			double mediaLikePerPost = currPsar.likesCount/currPsar.postFromPageCount;
//			double sharesPerPost = currPsar.sharesCount/currPsar.postFromPageCount;
//			double commentsPerAuthor = currPsar.commentsCount/uniqueAuthors;
//			double uniqueAuthorsPerPost = uniqueAuthors/currPsar.postFromPageCount;

			//DA CERCARE
//			double totFollower = 0;
//			String pageName = "";
//			Hashtable<String, String> baseInfo = PlusUtils.getBaseInfo(currKey);
//			pageName = baseInfo.get("displayname");
			
			//PRESENTI NEL DB
			//Già presenti nel DB
			String sindacoName = "";
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Filter DBInfo = new FilterPredicate(Constants.PL_ID_FIELD, FilterOperator.EQUAL, currKey);
			Query q = new Query(tabAnag).setFilter(DBInfo);
			PreparedQuery pq = datastore.prepare(q);
			for (Entity ent : pq.asIterable()) {
				sindacoName = (String) ent.getProperty("nome");
			}

			dataCSV = dataCSV + sindacoName + ";" + currKey + ";" +
					currPsar.postFromPageCount + ";" + currPsar.commentsCount + ";" +
					uniqueAuthors + ";" + currPsar.likesCount + ";" + currPsar.sharesCount + "\n";

		}

		return dataCSV;		
	}


	private Hashtable<String, PSARData> aggregatePsarData(ArrayList<PSARData> psarData) {
		Hashtable<String, PSARData> result = new Hashtable<String, PSARData>();
		Iterator<PSARData> iter = psarData.iterator();
		while (iter.hasNext()) {
			PSARData curr = iter.next();
			if (result.containsKey(curr.pageId)) {
				PSARData alreadyPresentData = result.get(curr.pageId);
				alreadyPresentData.commentsCount = alreadyPresentData.commentsCount + curr.commentsCount;
				alreadyPresentData.likesCount = alreadyPresentData.likesCount + curr.likesCount;
				alreadyPresentData.postFromPageCount = alreadyPresentData.postFromPageCount + curr.postFromPageCount;
				alreadyPresentData.sharesCount = alreadyPresentData.sharesCount + curr.sharesCount;
				alreadyPresentData.authors.addAll(curr.authors);
			} else {
				result.put(curr.pageId, curr);
			}
		}
		return result;
	}



}
