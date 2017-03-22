package it.osg.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusRequestInitializer;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;
import com.google.api.services.plus.model.Comment;
import com.google.api.services.plus.model.CommentFeed;
import com.google.api.services.plus.model.Person;

import facebook4j.Post;

public class PlusUtils {

	//private static final String API_KEY = "AIzaSyB8YOf4t4cwdahoOgb8QPyLF4asGXtNlvo";
	private static final long MAX_RESULT = 10L;
	
	private static Plus plus = null;

	public static Plus getPlus() {
		if (plus != null) {
			return plus;
		}
		HttpTransport httpTransport = new UrlFetchTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		Plus pl = new Plus.Builder(httpTransport, jsonFactory, null).setApplicationName("TEST").setGoogleClientRequestInitializer(new PlusRequestInitializer(Constants.PLUS_API_KEY)).build();
		plus = pl;
		return plus;
	}

	public static ArrayList<Activity> getAllPosts(String pageId, Date f, Date t, String[] campi) {
		Plus plus = getPlus();
		ArrayList<Activity> result = new ArrayList<Activity>();
		try {
			ActivityFeed myActivityFeed = plus.activities().list(pageId, "public").setMaxResults(MAX_RESULT).execute();
			List<Activity> myActivities = myActivityFeed.getItems();
			while (myActivities != null && myActivities.size() > 0) {
				if (myActivities.get(0).getPublished().getValue() < f.getTime()) {
					break;
				}
//				if (myActivities.get(myActivities.size() - 1).getPublished().getValue() > t.getTime()) {
//					break;
//				}
				for (Activity a : myActivities) {
					long datePublished = a.getPublished().getValue();
					if (datePublished > f.getTime() && datePublished < t.getTime()) {
						result.add(a);
					}					
				}

				if (myActivityFeed.getNextPageToken() == null) {
					break;
				}

				myActivityFeed = plus.activities().list(pageId, "public").setMaxResults(MAX_RESULT).setPageToken(myActivityFeed.getNextPageToken()).execute();
				myActivities = myActivityFeed.getItems(); 

			}


		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


	public static ArrayList<Comment> getComments (ArrayList<Activity> activities) {
		ArrayList<Comment> result = new ArrayList<Comment>();
		Iterator<Activity> iterActivities = activities.iterator();
		while (iterActivities.hasNext()) {
			Activity curAct = iterActivities.next();
			ArrayList<Comment> currComments = getAllComments(curAct);
			result.addAll(currComments);
		}
		return result;
	}
	

	public static ArrayList<Comment> getAllComments(Activity act) {
		Plus plus = getPlus();
		ArrayList<Comment> result = new ArrayList<Comment>();
		try {
			CommentFeed comments = plus.comments().list(act.getId()).setMaxResults(MAX_RESULT).execute();
			List<Comment> comms = comments.getItems();
			while (comms != null) {
				result.addAll(comms);
				if (comments.getNextPageToken() == null) {
					break;
				}
				comments = plus.comments().list(act.getId()).setMaxResults(MAX_RESULT).setPageToken(comments.getNextPageToken()).execute();
				comms = comments.getItems();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static ArrayList<String> getUniqueAuthors (ArrayList<Comment> comments) {

		ArrayList<String> result = new ArrayList<String>();

		//Per tutti i commenti accumulati prelevo gli autori
		Iterator<Comment> iterComm = comments.iterator();
		while (iterComm.hasNext()) {
			Comment currComm = iterComm.next();
			result.add(currComm.getActor().getId());
		}

		return ArrayUtils.removeDuplicate(result);
	}
	
	
	public static double getPluses (ArrayList<Activity> act) {
		double result = 0;
		Iterator<Activity> iterAct = act.iterator();
		while (iterAct.hasNext()) {
			Activity curAct = iterAct.next();
			if (curAct.getObject().getPlusoners().getTotalItems() != null) {
				result = result + curAct.getObject().getPlusoners().getTotalItems();
			}
		}
		return result;
	}
	
	public static double getShares (ArrayList<Activity> act) {
		double result = 0;
		Iterator<Activity> iterAct = act.iterator();
		while (iterAct.hasNext()) {
			Activity curAct = iterAct.next();
			if (curAct.getObject().getResharers().getTotalItems() != null) {
				result = result + curAct.getObject().getResharers().getTotalItems();
			}
		}
		return result;
	}
	
	public static Hashtable<String, String> getBaseInfo (String userId) {
		Hashtable<String, String> result = new Hashtable<String, String>();
		Plus plus = getPlus();
		try {
			Person mePerson = plus.people().get(userId).execute();
			result.put("displayname", mePerson.getDisplayName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
