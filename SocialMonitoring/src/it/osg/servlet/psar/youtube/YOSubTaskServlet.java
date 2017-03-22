package it.osg.servlet.psar.youtube;

import it.osg.servlet.SubTaskServlet;
import it.osg.utils.Constants;
import it.osg.utils.YouTubeUtils;

import java.math.BigInteger;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.google.api.services.youtube.model.Activity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class YOSubTaskServlet extends SubTaskServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	protected void runSubTask(String idTransaction, String pageId, Date f, Date t) {
	
		//Get all Activities
		List<Activity> activities = YouTubeUtils.getActivities(pageId, f, t);
		double activitiescount = activities.size();
		
		//Get all Interactions
		Hashtable<String, Double> interactions = YouTubeUtils.getAllUserInteraction(activities);

		//TODO Get all Unique Authors of Comments

		//SAVE OUTPUT TO DATASTORE
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity currEntity = new Entity(Constants.TASK_TABLE);
		currEntity.setProperty(Constants.ID_TRANSACTION_FIELD, idTransaction);
		currEntity.setProperty("pageId", pageId);
		currEntity.setProperty("activitiescount", activitiescount);
		currEntity.setProperty("viewcount", interactions.get("viewcount"));
		currEntity.setProperty("likecount", interactions.get("likecount"));
		currEntity.setProperty("dislikecount", interactions.get("dislikecount"));
		currEntity.setProperty("favouritecount", interactions.get("favouritecount"));
		currEntity.setProperty("commentcount", interactions.get("commentcount"));

		datastore.put(currEntity);

	}



}
