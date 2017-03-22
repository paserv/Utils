package it.osg.stream;

import facebook4j.Comment;
import facebook4j.FacebookException;
import facebook4j.Post;
import it.osg.utils.DateUtils;
import it.osg.utils.FacebookUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class FacebookStreamParser {

	public static void main(String[] args) throws FacebookException {

//		String id = "258372870902870";//Federico Pizzarotti
//		String id = "63360159216";//Ignazio Marino
//		String id = "113335124914";//Matteo Renzi
//		String id = "53388213256";//Vincenzo De Luca
//		String id = "112352038802143";//Giuliano Pisapia
//		String id = "53505098083";//Michele Emiliano
//		String id = "61657367059";//Luigi De Magistris
//		String id = "49957446932";//Renato Accorinti
//		String id = "160694770110";//Graziano Delrio
//		String id = "32377918793";//Flavio Zanonato
		
		String id = "115066385228297";//Poste Italiane
		
		String from = "01-01-2014 00:00:00";
		String to = "31-01-2014 23:59:59";


		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File("out.csv"));
			
			Date f = null;
			Date t = null;
			try {
				f = DateUtils.parseDateAndTime(from);
				t = DateUtils.parseDateAndTime(to);
			} catch (ParseException e) {
				e.printStackTrace();
			}


			ArrayList<Post> posts = FacebookUtils.getAllPosts(id, f, t, null);
			Iterator<Post> postIter = posts.iterator();

			while (postIter.hasNext()) {
				Post currPost = postIter.next();
				if (currPost.getMessage() != null) {
					String message = currPost.getMessage().replaceAll ("[ \\p{Punct}]", " ");
					message = message.replaceAll("\n", "");
					message = message.replaceAll("\r\n|\r|\n", " ");
					String msg = message.toLowerCase();

					System.out.print(currPost.getId() + ";" + id + ";" + "POST" + ";" + DateUtils.formatDate(currPost.getCreatedTime(), "dd/MM/yyyy") + ";" + DateUtils.formatDate(currPost.getCreatedTime(), "HH:mm:ss") + ";" + msg + ";");
					fos.write((currPost.getId() + ";" + id + ";" + "POST" + ";" + DateUtils.formatDate(currPost.getCreatedTime(), "dd/MM/yyyy") + ";" + DateUtils.formatDate(currPost.getCreatedTime(), "HH:mm:ss") + ";" + msg + ";").getBytes());
					if (currPost.getFrom().getId().toString() != null && currPost.getFrom().getId().toString().equalsIgnoreCase(id)) {
						System.out.println("OWN");
						fos.write(("OWN\n").getBytes());
					} else {
						System.out.println("FAN");
						fos.write(("FAN\n").getBytes());
					}
				}

				ArrayList<Comment> comments = FacebookUtils.getAllComments(currPost);
				Iterator<Comment> commIter = comments.iterator();
				while (commIter.hasNext()) {
					Comment currComment = commIter.next();
					if (currComment.getMessage() != null) {
						String messageComm = currComment.getMessage().replaceAll ("[ \\p{Punct}]", " ");
						messageComm = messageComm.replaceAll("\n", "");
						messageComm = messageComm.replaceAll("\r\n|\r|\n", " ");
						String msg = messageComm.toLowerCase();

						System.out.print(currComment.getId() + ";" + id + ";" + "COMMENT" + ";" + DateUtils.formatDate(currComment.getCreatedTime(), "dd/MM/yyyy") + ";" + DateUtils.formatDate(currComment.getCreatedTime(), "HH:mm:ss") + ";" + msg + ";");
						fos.write((currComment.getId() + ";" + id + ";" + "COMMENT" + ";" + DateUtils.formatDate(currComment.getCreatedTime(), "dd/MM/yyyy") + ";" + DateUtils.formatDate(currComment.getCreatedTime(), "HH:mm:ss") + ";" + msg + ";").getBytes());
						if (currComment.getFrom().getId().toString() != null && currComment.getFrom().getId().toString().equalsIgnoreCase(id)) {
							System.out.println("OWN");
							fos.write(("OWN\n").getBytes());
						} else {
							System.out.println("FAN");
							fos.write(("FAN\n").getBytes());
						}
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}


	}

}
