package it.osg.runnable;

import java.util.ArrayList;
import java.util.Iterator;

import facebook4j.Comment;
import facebook4j.Post;
import it.osg.data.Hashtags;
import it.osg.utils.FacebookUtils;
import it.queue.RunnableQueue;

public class RunnableQueueImpl4Hashtags extends RunnableQueue {

	private Hashtags tags;
	private Post post;
	
	public RunnableQueueImpl4Hashtags(Post post, Hashtags tags) {
		super();
		this.tags = tags;
		this.post = post;
	}

	@Override
	public void executeJob() throws Exception {

		String msg = post.getMessage();

		if (msg != null) {
			/*Clean Post Message*/
			String message = msg.replaceAll ("[ \\p{Punct}]", " ");
			message = message.replaceAll("\n", "");
			message = message.replaceAll("\r\n|\r|\n", " ");
			String finalMsg = message.toLowerCase();

			/*Split and add to multiset*/
			String[] splitted = finalMsg.split(" ");
			for (int j = 1; j < splitted.length; j++) {
				if (splitted[j].charAt(0) == '#') {
					this.tags.addToMultiset(splitted[j]);
				}
			}

			/*Get Comments*/
			ArrayList<Comment> comments = FacebookUtils.getAllComments(post);
			Iterator<Comment> commIter = comments.iterator();
			while (commIter.hasNext()) {
				Comment currComment = commIter.next();
				if (currComment.getMessage() != null) {
					/*Clean Comment Message*/
					String messageComm = currComment.getMessage().replaceAll ("[ \\p{Punct}]", " ");
					messageComm = messageComm.replaceAll("\n", "");
					messageComm = messageComm.replaceAll("\r\n|\r|\n", " ");
					String finalCommentMsg = messageComm.toLowerCase();

					/*Split and add to multiset*/
					String[] splittedComment = finalCommentMsg.split(" ");
					for (int j = 1; j < splittedComment.length; j++) {
						if (splittedComment[j].charAt(0) == '#') {
							this.tags.addToMultiset(splittedComment[j]);
						}
					}

				}
			}
		}

	}

	@Override
	public void rollback() {
		try {
			Thread.sleep(600000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
