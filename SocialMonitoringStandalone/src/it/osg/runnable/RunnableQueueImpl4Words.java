package it.osg.runnable;

import java.util.ArrayList;
import java.util.Iterator;

import facebook4j.Comment;
import facebook4j.Post;
import it.osg.data.Words;
import it.osg.utils.FacebookUtils;
import it.queue.RunnableQueue;

public class RunnableQueueImpl4Words extends RunnableQueue {

	private Post post;
	private Words words;
	
	public RunnableQueueImpl4Words(Post post, Words words) {
		super();
		this.post = post;
		this.words = words;
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
//			String[] splitted = finalMsg.split(" ");
//			for (int j = 0; j < splitted.length; j++) {
				this.words.addWord(finalMsg);
//			}

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
//					String[] splittedComment = finalCommentMsg.split(" ");
//					for (int j = 0; j < splittedComment.length; j++) {
						this.words.addWord(finalCommentMsg);
//					}

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
