package it.osg.runnable;

import facebook4j.Comment;
import facebook4j.FacebookException;
import facebook4j.Like;
import facebook4j.Post;
import it.osg.data.PSAR;
import it.osg.utils.FacebookUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class AtomicPostJob implements Runnable {

	private String userID;
	private String postID;
	private PSAR data;
	private ExecutorService executor;

	public AtomicPostJob (String userID, String postID, PSAR data, ExecutorService executor) {
		this.userID = userID;
		this.postID = postID;
		this.data = data;
		this.executor = executor;
	}

	@Override
	public void run() {

//		System.out.println("Running Post ID: " + postID);
		Post completePost = null;
		try {
			completePost = FacebookUtils.getPost(postID);
		} catch (FacebookException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int commentNum = 0;
		int likesNum = 0;
		int sharesNum = 0;
		int commentsPageToPagePost = 0;
		
		int commentToFanPost = 0;
		int commentPageToFanPost = 0;
		
		boolean isPagePost = false;
		
		try {
			if (completePost.getFrom().getId().equals(userID) && completePost.getMessage() != null) {
				isPagePost = true;
				data.getPostFromPage().incrementAndGet();
				
				ArrayList<Comment> commentsList = FacebookUtils.getAllComments(completePost);
				commentNum = commentsList.size();
				data.addComments(commentNum);
				
				ArrayList<Like> likesList = FacebookUtils.getAllLikes(completePost);
				likesNum = likesList.size();
				data.addLikes(likesNum);
				
				sharesNum = FacebookUtils.getSharesInteger(completePost);
				data.addShares(sharesNum);
				
				commentsPageToPagePost = FacebookUtils.getCommentsFromIdCountInteger(userID, commentsList);
				data.addCommnetsFromPageToPostFromPage(commentsPageToPagePost);
				
			} else {
				data.getPostFromFan().incrementAndGet();
				
				ArrayList<Comment> commentsToPostFromFanList = FacebookUtils.getAllComments(completePost);
				commentToFanPost = commentsToPostFromFanList.size();
				data.addCommentsToPostFromFan(commentToFanPost);
				
				commentPageToFanPost = FacebookUtils.getCommentsFromIdCountInteger(userID, commentsToPostFromFanList);
				data.addCommnetsFromPageToPostFromFan(commentPageToFanPost);
				
			}
		} catch (Exception e) {
//			System.out.println("ERRORE");
//			if (isPagePost) {
//				data.getPostFromPage().decrementAndGet();
//				data.subComments(commentNum);
//				data.subComments(likesNum);
//				data.subComments(sharesNum);
//				data.subCommnetsFromPageToPostFromPage(commentsPageToPagePost);
//			} else {
//				data.getPostFromFan().decrementAndGet();
//				data.subCommnetsFromPageToPostFromFan(commentToFanPost);
//				data.subCommnetsFromPageToPostFromFan(commentPageToFanPost);
//			}
//			Runnable retry = new AtomicPostJob(userID, postID, data, executor);
//			executor.execute(retry);
			System.out.println("ERRORE");
			e.printStackTrace();
		}
		
	}

}
