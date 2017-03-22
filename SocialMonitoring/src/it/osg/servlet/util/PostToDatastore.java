package it.osg.servlet.util;

import facebook4j.Comment;
import facebook4j.Post;
import it.osg.utils.DateUtils;
import it.osg.utils.FacebookUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

public class PostToDatastore extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		String id = req.getParameter("id");
		String from = req.getParameter("from");
		String to = req.getParameter("to");
				
		Date f = null;
		Date t = null;
		try {
			f = DateUtils.parseDateAndTime(from);
			t = DateUtils.parseDateAndTime(to);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		ArrayList<Post> posts = FacebookUtils.getAllPosts(id, f, t, null);
		Iterator<Post> postIter = posts.iterator();
		
		while (postIter.hasNext()) {
			Post currPost = postIter.next();
			if (currPost.getMessage() != null) {
				Entity currEntity = new Entity("postFB", currPost.getId());
				currEntity.setUnindexedProperty("idSindaco", id);
				currEntity.setUnindexedProperty("INTE", "POST");
				currEntity.setUnindexedProperty("createdTime", currPost.getCreatedTime());
				String message = currPost.getMessage().replaceAll ("[ \\p{Punct}]", " ");
				String msg = message.toLowerCase();
				currEntity.setUnindexedProperty("message", new Text(msg));
				if (currPost.getFrom().getId().toString() != null && currPost.getFrom().getId().toString().equalsIgnoreCase(id)) {
					currEntity.setUnindexedProperty("AUTH", "OWN");
				} else {
					currEntity.setUnindexedProperty("AUTH", "FAN");
				}
				datastore.put(currEntity);
			}
			
			ArrayList<Comment> comments = FacebookUtils.getAllComments(currPost);
			Iterator<Comment> commIter = comments.iterator();
			while (commIter.hasNext()) {
				Comment currComment = commIter.next();
				if (currComment.getMessage() != null) {
					Entity currEntity = new Entity("postFB", currComment.getId());
					currEntity.setUnindexedProperty("idSindaco", id);
					currEntity.setUnindexedProperty("INTE", "COMMENT");
					currEntity.setUnindexedProperty("createdTime", currComment.getCreatedTime());
					String messageComm = currComment.getMessage().replaceAll ("[ \\p{Punct}]", " ");
					String msg = messageComm.toLowerCase();
					currEntity.setUnindexedProperty("message", new Text(msg));
					if (currComment.getFrom().getId().toString() != null && currComment.getFrom().getId().toString().equalsIgnoreCase(id)) {
						currEntity.setUnindexedProperty("AUTH", "OWN");
					} else {
						currEntity.setUnindexedProperty("AUTH", "FAN");
					}
					datastore.put(currEntity);
				}
			}
		}
		
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}

}
