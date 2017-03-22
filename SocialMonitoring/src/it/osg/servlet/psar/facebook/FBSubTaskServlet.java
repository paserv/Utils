package it.osg.servlet.psar.facebook;

import facebook4j.Comment;
import facebook4j.Like;
import facebook4j.Post;
import it.osg.servlet.SubTaskServlet;
import it.osg.utils.Constants;
import it.osg.utils.FacebookUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

public class FBSubTaskServlet extends SubTaskServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	protected void runSubTask(String idTransaction, String pageId, Date f, Date t) {

		//OUTPUT DATA
		double totParzPostFromPage = 0;
		double totParzPostFromFan = 0;
		double totParzComments = 0;
		String authors = "";
		double totParzLikes = 0;
		double totParzShares = 0;
		
		double totParzCommentsToPostFromFan = 0;
		double totParzCommnetsFromPageToPostFromPage = 0;
		double totParzCommnetsFromPageToPostFromPageNoDuplicate = 0;
		double totParzCommnetsFromPageToPostFromFan = 0;
		double totParzCommnetsFromPageToPostFromFanNoDuplicate = 0;

		//Get all Post
		ArrayList<Post> posts = FacebookUtils.getAllPosts(pageId, f, t, null);

		ArrayList<Post> postFromPage = new ArrayList<Post>();
		ArrayList<Post> postFromFan = new ArrayList<Post>();

		Iterator<Post> iterPost = posts.iterator();
		while (iterPost.hasNext()) {
			Post currPost = iterPost.next();
			if (currPost.getFrom().getId().equals(pageId) && currPost.getMessage() != null) {
				postFromPage.add(currPost);
			} else {
				postFromFan.add(currPost);
			}
		}

		
		//P^{x}[i,j] + S^{x}[i,j] Link, Status, Photo, Video from page 
		totParzPostFromPage = postFromPage.size();
		totParzPostFromFan = postFromFan.size();

		//Comments to Post from page
		ArrayList<Comment> comments = FacebookUtils.getComments(postFromPage);
		//C^{x}_{Post^{x}}[i,j] 
		totParzComments = comments.size();
		
		//Get all Comments from FAN
		ArrayList<Comment> commentsToPostFromFan = FacebookUtils.getComments(postFromFan);
		totParzCommentsToPostFromFan = commentsToPostFromFan.size();

		//Get all Unique Authors of Comments
//		ArrayList<String> uniqueAuth = FacebookUtils.getUniqueAuthors(comments);
//		Iterator<String> iter = uniqueAuth.iterator();
//		while (iter.hasNext()) {
//			authors = authors + iter.next() + ",";
//		}

		//Get all Likes to Posts
		ArrayList<Like> likes = FacebookUtils.getLikes(postFromPage);
		//L_{P^{x}}[i,j] 
		totParzLikes = likes.size();

		//S_{P^{x}}[i,j] Get all Shares to Posts
		totParzShares = FacebookUtils.getShares(postFromPage);

		//C^{x}_{Post^{x}}[i,j]		
		totParzCommnetsFromPageToPostFromPage = FacebookUtils.getCommentsFromIdCount(pageId, comments);
		//C_{Post^{z\neq x}}^{x}[i,j]
		totParzCommnetsFromPageToPostFromFan = FacebookUtils.getCommentsFromIdCount(pageId, commentsToPostFromFan);
		

		//SAVE OUTPUT TO DATASTORE
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity currEntity = new Entity(Constants.TASK_TABLE);
		currEntity.setProperty(Constants.ID_TRANSACTION_FIELD, idTransaction);
		currEntity.setProperty("pageId", pageId);

		currEntity.setProperty("totParzPostFromPage", totParzPostFromPage);
		currEntity.setProperty("totParzPostFromFan", totParzPostFromFan);
		currEntity.setProperty("totParzComments", totParzComments);
//		currEntity.setProperty("authors", new Text(authors));
		currEntity.setProperty("totParzLikes", totParzLikes);
		currEntity.setProperty("totParzShares", totParzShares);
		
		currEntity.setProperty("totParzCommentsToPostFromFan", totParzCommentsToPostFromFan);
		currEntity.setProperty("totParzCommnetsFromPageToPostFromPage", totParzCommnetsFromPageToPostFromPage);
		currEntity.setProperty("totParzCommnetsFromPageToPostFromFan", totParzCommnetsFromPageToPostFromFan);

		datastore.put(currEntity);

	}



}
