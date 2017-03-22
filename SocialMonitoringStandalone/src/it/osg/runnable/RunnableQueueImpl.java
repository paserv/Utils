package it.osg.runnable;

import java.util.ArrayList;

import facebook4j.Comment;
import facebook4j.Like;
import facebook4j.Post;
import it.osg.data.PSAR;
import it.osg.utils.Configuration;
import it.osg.utils.FacebookUtils;
import it.queue.RunnableQueue;

public class RunnableQueueImpl extends RunnableQueue {

	private String userID;
	private String postID;
	private PSAR data;

	public RunnableQueueImpl(String userID, String postID, PSAR data) {
		super();
		this.userID = userID;
		this.postID = postID;
		this.data = data;
	}

	@Override
	public void executeJob() throws Exception {

		Post completePost = FacebookUtils.getPost(this.postID);

		if (completePost == null || completePost.getFrom() == null || completePost.getFrom().getId() == null || completePost.getMessage() == null) {
			return;
		}
		
		int commentNum = 0;
		int likesNum = 0;
		int sharesNum = 0;
		int commentsPageToPagePost = 0;

		int commentToFanPost = 0;
		int commentPageToFanPost = 0;

		if (completePost.getFrom().getId().equals(this.userID)) {

			ArrayList<Comment> commentsList = FacebookUtils.getAllComments(completePost);
			commentNum = commentsList.size();

			ArrayList<Like> likesList = FacebookUtils.getAllLikes(completePost);
			likesNum = likesList.size();

			sharesNum = FacebookUtils.getSharesInteger(completePost);

			commentsPageToPagePost = FacebookUtils.getCommentsFromIdCountInteger(this.userID, commentsList);

			data.getPostFromPage().incrementAndGet();
			data.addComments(commentNum);
			data.addLikes(likesNum);
			data.addShares(sharesNum);
			data.addCommnetsFromPageToPostFromPage(commentsPageToPagePost);

		} else {

			ArrayList<Comment> commentsToPostFromFanList = FacebookUtils.getAllComments(completePost);
			commentToFanPost = commentsToPostFromFanList.size();

			commentPageToFanPost = FacebookUtils.getCommentsFromIdCountInteger(this.userID, commentsToPostFromFanList);

			data.getPostFromFan().incrementAndGet();
			data.addCommentsToPostFromFan(commentToFanPost);
			data.addCommnetsFromPageToPostFromFan(commentPageToFanPost);

		}


	}

	@Override
	public void rollback() {
		try {
			Thread.sleep(Configuration.ROLLBACK_SLEEP);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
