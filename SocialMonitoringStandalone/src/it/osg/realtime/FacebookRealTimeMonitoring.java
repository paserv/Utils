package it.osg.realtime;

import facebook4j.Comment;
import facebook4j.FacebookException;
import facebook4j.Like;
import facebook4j.Post;
import it.osg.utils.Constants;
import it.osg.utils.DatastoreServiceUtil;
import it.osg.utils.DateUtils;
import it.osg.utils.FacebookUtils;
import it.osg.utils.SpreadsheetUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FacebookRealTimeMonitoring {

	private final static Logger LOGGER = Logger.getLogger(FacebookRealTimeMonitoring.class .getName()); 
	
	public static void main(String[] args) throws FacebookException {
		
		LOGGER.setLevel(Level.INFO); 
		
		//args[0] = spreadsheetID -> 0ApFZC7m0E5ixdGViY2RoQlJXRHFxTnlmeHZ3MzYyUnc
		//args[1] = numero giorni monitorine -> giorni
		//args[2] = idtransaction -> pablo
		//args[3] = monitoring interval -> minuti
		
		String spreadsheetID = args[0];
		int monitoringInterval = Integer.valueOf(args[1]);
		String sessionName = args[2];
		long monitoringSleep = Long.valueOf(args[3]);
		
		try {
			Thread.sleep(monitoringSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//GET MONITORED ENTITIES
		SpreadsheetUtils su = new SpreadsheetUtils(Constants.GMAIL_USER, Constants.GMAIL_PSW, spreadsheetID);
		Hashtable<String,String> ids = su.getHashrange();
		
		//INITIALIZE SESSION
		Enumeration<String> keys = ids.keys();
		while (keys.hasMoreElements()) {
			String currID = keys.nextElement();
			String name = ids.get(currID);
			DatastoreServiceUtil.registerEntity(name, currID, sessionName);
		}
		
		//GET CURRENT DATE
		Date from = new Date();
		Date to = DateUtils.addDayToDate(from, monitoringInterval);
		
		Date currentTime = new Date();
		
		while (DateUtils.afterNoTime(to, currentTime)) {
			currentTime = new Date();
			Enumeration<String> keys2 = ids.keys();
			while (keys2.hasMoreElements()) {
				String currID = keys2.nextElement();
				String name = ids.get(currID);
				LOGGER.info("Analysing: " + currID + " - " + name);
				
				//GET POSTS
				ArrayList<Post> posts = new ArrayList<Post>();
				posts =	FacebookUtils.getAllPosts(currID, from, currentTime, null);

				ArrayList<Post> postFromPageList = new ArrayList<Post>();
				ArrayList<Post> postFromFanList = new ArrayList<Post>();

				Iterator<Post> iterPost = posts.iterator();
				while (iterPost.hasNext()) {
					Post currPost = iterPost.next();
					if (currPost.getFrom().getId().equals(currID) && currPost.getMessage() != null) {
						postFromPageList.add(currPost);
					} else {
						postFromFanList.add(currPost);
					}
				}
				
				LOGGER.info(currID + " - " + name + " Own Post: " + postFromPageList.size());
				LOGGER.info(currID + " - " + name + " Other Post: " + postFromFanList.size());
				
				//GET COMMENTS
				ArrayList<Comment> commentsList = FacebookUtils.getComments(postFromPageList);
				LOGGER.info(currID + " - " + name + " Own Comments: " + commentsList.size());

				//Get all Comments from FAN
				ArrayList<Comment> commentsToPostFromFanList = FacebookUtils.getComments(postFromFanList);
				LOGGER.info(currID + " - " + name + " Comments To Post From Fan: " + commentsToPostFromFanList.size());

				//Get all Likes to Posts
				ArrayList<Like> likesList = FacebookUtils.getLikes(postFromPageList);
				LOGGER.info(currID + " - " + name + " Likes: " + likesList.size());

				//Get all Shares to Posts
				long shares = FacebookUtils.getShares(postFromPageList);
				LOGGER.info(currID + " - " + name + " Shares: " + shares);

				//C^{x}_{Post^{x}}[i,j]		
				long commnetsFromPageToPostFromPage = FacebookUtils.getCommentsFromIdCount(currID, commentsList);
				//C_{Post^{z\neq x}}^{x}[i,j]
				long commnetsFromPageToPostFromFan = FacebookUtils.getCommentsFromIdCount(currID, commentsToPostFromFanList);
				long totOwnComment = commnetsFromPageToPostFromFan + commnetsFromPageToPostFromPage;
				LOGGER.info(currID + " - " + name + " Tot Own Comment: " + totOwnComment);
				
				//SAVE TO DATASTORE
				LOGGER.info(currID + " - " + name + " Saving to Datastore");
				DatastoreServiceUtil.saveProperty(currID, sessionName, "numOwnPost", Long.valueOf(postFromPageList.size()));
				DatastoreServiceUtil.saveProperty(currID, sessionName, "numOtherPost", Long.valueOf(postFromFanList.size()));
				DatastoreServiceUtil.saveProperty(currID, sessionName, "numCommToOwnPost", Long.valueOf(commentsList.size()));
				DatastoreServiceUtil.saveProperty(currID, sessionName, "numCommToFanPost", Long.valueOf(commentsToPostFromFanList.size()));
				DatastoreServiceUtil.saveProperty(currID, sessionName, "numLikeToOwnPost", Long.valueOf(likesList.size()));
				DatastoreServiceUtil.saveProperty(currID, sessionName, "numShareToOwnPost", shares);
				DatastoreServiceUtil.saveProperty(currID, sessionName, "numOwnComm", totOwnComment);
				LOGGER.info(currID + " - " + name + " Saved to Datastore");
				
			}
		}
		
	}
	
}
