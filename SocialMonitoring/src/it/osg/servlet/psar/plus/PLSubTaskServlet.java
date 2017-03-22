package it.osg.servlet.psar.plus;

import it.osg.servlet.SubTaskServlet;
import it.osg.utils.Constants;
import it.osg.utils.PlusUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.Comment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

public class PLSubTaskServlet extends SubTaskServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	protected void runSubTask(String idTransaction, String pageId, Date f, Date t) {

		//OUTPUT DATA
		double totParzActFromPage = 0;
		double totParzComments = 0;
		String authors = "";
		double totParzPluses = 0;
		double totParzShares = 0;

		
		//Get all Post
		ArrayList<Activity> activities = PlusUtils.getAllPosts(pageId, f, t, null);
		totParzActFromPage = activities.size();
		
		//Get all Comments
		ArrayList<Comment> comments = PlusUtils.getComments(activities);
		totParzComments = comments.size();

		//Get all Unique Authors of Comments
		ArrayList<String> uniqueAuth = PlusUtils.getUniqueAuthors(comments);
		Iterator<String> iter = uniqueAuth.iterator();
		while (iter.hasNext()) {
			authors = authors + iter.next() + ",";
		}

		//Get all Likes to Posts
		totParzPluses = PlusUtils.getPluses(activities);

		//Get all Shares to Posts
		totParzShares = PlusUtils.getShares(activities);


		//SAVE OUTPUT TO DATASTORE
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity currEntity = new Entity(Constants.TASK_TABLE);
		currEntity.setProperty(Constants.ID_TRANSACTION_FIELD, idTransaction);
		currEntity.setProperty("pageId", pageId);

		currEntity.setProperty("totParzActFromPage", totParzActFromPage);
		currEntity.setProperty("totParzComments", totParzComments);
		if (!authors.equalsIgnoreCase("")) {
			currEntity.setProperty("authors", new Text(authors));
		}
		currEntity.setProperty("totParzPluses", totParzPluses);
		currEntity.setProperty("totParzShares", totParzShares);

		datastore.put(currEntity);

	}



}
