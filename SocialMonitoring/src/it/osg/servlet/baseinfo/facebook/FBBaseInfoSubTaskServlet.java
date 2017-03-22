package it.osg.servlet.baseinfo.facebook;

import java.util.Date;
import java.util.Iterator;

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

import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;

import it.osg.servlet.SubTaskServlet;
import it.osg.utils.Constants;
import it.osg.utils.DateUtils;
import it.osg.utils.FacebookUtils;

public class FBBaseInfoSubTaskServlet extends SubTaskServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void runSubTask(String idTranscation, String pageId, Date from, Date to) {
		String endDate = "";
		try {
			ResponseList<Post> posts = FacebookUtils.getFB().getFeed(pageId, new Reading().since(from).until(to));
			Iterator<Post> iterPost = posts.iterator();
			while (iterPost.hasNext()) {
				Post currPost = iterPost.next();
				if (currPost != null && currPost.getFrom() != null && currPost.getFrom().getId() != null && currPost.getFrom().getId().equalsIgnoreCase(pageId)) {
					endDate = DateUtils.formatDate(currPost.getCreatedTime());
					break;
				}
			}
		} catch (FacebookException e) {
			e.printStackTrace();
		}

		
		long fanCount = 0L;
		long talkingAboutCount = 0L;
		String baseInfoDate = "";
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q;
		Filter fromFilter = new FilterPredicate("date", FilterOperator.LESS_THAN_OR_EQUAL, to);
		Filter idPageFilter = new FilterPredicate("idFacebook", FilterOperator.EQUAL, pageId);
		Filter compositeFilter = CompositeFilterOperator.and(idPageFilter, fromFilter);
		q = new Query(Constants.FACEBOOK_MONITOR_TABLE).setFilter(compositeFilter).addSort("date", SortDirection.DESCENDING);
		PreparedQuery pq = datastore.prepare(q);
		for (Entity ent : pq.asIterable()) {
			if ( ent.getProperty("like_count") != null) {
				fanCount = (Long) ent.getProperty("like_count");
				talkingAboutCount = (Long) ent.getProperty("talking_about_count");
				baseInfoDate = DateUtils.formatDate(((Date) ent.getProperty("date")));
				break;
			}
		}
		
		
		long fanCountStart = 0L;
		long talkingAboutCountStart = 0L;
		String baseInfoDateStart = "";
		fromFilter = new FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, from);
		idPageFilter = new FilterPredicate("idFacebook", FilterOperator.EQUAL, pageId);
		compositeFilter = CompositeFilterOperator.and(idPageFilter, fromFilter);
		q = new Query(Constants.FACEBOOK_MONITOR_TABLE).setFilter(compositeFilter).addSort("date", SortDirection.ASCENDING);
		pq = datastore.prepare(q);
		for (Entity ent : pq.asIterable()) {
			if ( ent.getProperty("like_count") != null) {
				fanCountStart = (Long) ent.getProperty("like_count");
				talkingAboutCountStart = (Long) ent.getProperty("talking_about_count");
				baseInfoDateStart = DateUtils.formatDate(((Date) ent.getProperty("date")));
				break;
			}
		}

		//SAVE OUTPUT TO DATASTORE
		Entity currEntity = new Entity(Constants.TASK_TABLE);
		currEntity.setProperty(Constants.ID_TRANSACTION_FIELD, idTranscation);
		currEntity.setProperty("pageId", pageId);

		currEntity.setProperty("endDate", endDate);
		
		currEntity.setProperty("fanCount", fanCount);
		currEntity.setProperty("talkingAboutCount", talkingAboutCount);
		currEntity.setProperty("baseInfoDate", baseInfoDate);
		
		currEntity.setProperty("fanCountStart", fanCountStart);
		currEntity.setProperty("talkingAboutCountStart", talkingAboutCountStart);
		currEntity.setProperty("baseInfoDateStart", baseInfoDateStart);

		datastore.put(currEntity);

	}



}
