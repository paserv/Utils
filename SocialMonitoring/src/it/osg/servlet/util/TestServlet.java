package it.osg.servlet.util;


import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Paging;
import facebook4j.Post;
import facebook4j.ResponseList;
import it.osg.utils.DateUtils;
import it.osg.utils.FacebookUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {


	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();

		Date from = null;
		Date to = null;
		try {
			from = DateUtils.parseDateAndTime("01-05-2014".substring(0,10));
			to = DateUtils.parseDateAndTime("03-05-2014".substring(0,10));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		Hashtable<String, String> result = new Hashtable<String, String>();

		ArrayList<Post> appoggio = new ArrayList<Post>();

		Facebook facebook = FacebookUtils.getFB();
		ResponseList<Post> postsByKey;
		try {
			postsByKey = facebook.searchPosts(req.getParameter("keyword"));
			appoggio.addAll(postsByKey);

			//Fetching Post
			Paging<Post> pagingPost = postsByKey.getPaging();

			while (true) {
				if (pagingPost != null) {
					ResponseList<Post> nextPosts;
					nextPosts = facebook.fetchNext(pagingPost);
					appoggio.addAll(nextPosts);
					pagingPost = nextPosts.getPaging();

				} else {
					break;
				}
			}

		} catch (FacebookException e1) {
			e1.printStackTrace();
		}


		Iterator<Post> iterAppoggio = appoggio.iterator();
		while(iterAppoggio.hasNext()) {
			Post curr = iterAppoggio.next();
			Date createdTime = curr.getCreatedTime();
			if (DateUtils.between(createdTime, from, to)) {
				result.put(curr.getId(), curr.getFrom().getId());
				out.println("Date: " + createdTime + " Id Post: " + curr.getId() + " Id Author: " + curr.getFrom().getId());
			}
			
		}

	


		//		Iterator<Post> postIter = posts.iterator();
		//		while (postIter.hasNext()) {
		//			Post currPost = postIter.next();
		//			if (currPost.getMessage() != null) {
		//				String currMessage = currPost.getMessage().replaceAll ("[ \\p{Punct}]", " ");
		//				out.println(currMessage + "<br>");
		//				ArrayList<Comment> comments = FacebookUtils.getAllComments(currPost);
		//				Iterator<Comment> commIter = comments.iterator();
		//				while (commIter.hasNext()) {
		//					Comment currComment = commIter.next();
		//					if (currComment.getMessage() != null) {
		//						String currCommMessage = currComment.getMessage().replaceAll ("[ \\p{Punct}]", " ");
		//						out.println(currCommMessage + "<br>");						
		//					}
		//				}
		//			}
		//		}


		//		out.println("CIAO");
		//		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		//		Query q;
		//		try {
		//			Filter fromFilter = new FilterPredicate("date", FilterOperator.LESS_THAN_OR_EQUAL, DateUtils.parseDateAndTime("30-10-2013 00:00:00"));
		//			Filter idPageFilter = new FilterPredicate("idFacebook", FilterOperator.EQUAL, "390316297742803");
		//			Filter compositeFilter = CompositeFilterOperator.and(idPageFilter, fromFilter);
		//			q = new Query(Constants.FACEBOOK_MONITOR_TABLE).setFilter(compositeFilter).addSort("date", SortDirection.DESCENDING);
		//			PreparedQuery pq = datastore.prepare(q);
		//			for (Entity ent : pq.asIterable()) {
		//				if ( ent.getProperty("like_count") != null) {
		//					out.println("like_count: " + (Long) ent.getProperty("like_count"));
		//					out.println("talking_about_count: " + ent.getProperty("talking_about_count"));
		//					out.println("date: " + ent.getProperty("date"));
		//				}
		//			}
		//		} catch (ParseException e) {
		//			e.printStackTrace();
		//		}


		//		Date f = null;
		//		Date t = null;
		//		try {
		//
		//			f = DateUtils.parseDateAndTime((String) "01-09-2013 00:00:00");
		//			t = DateUtils.parseDateAndTime((String) "30-09-2013 00:00:00");
		////			f = DateUtils.parseDateAndTime(req.getParameter("from"));
		////			t = DateUtils.parseDateAndTime(req.getParameter("to"));
		//		} catch (ParseException e) {
		//			e.printStackTrace();
		//		}
		//		
		//		ArrayList<Post> posts = FacebookUtils.getAllPosts("113335124914", f, t, null);
		//		Iterator<Post> iter = posts.iterator();
		//		while (iter.hasNext()) {
		//			Post curr = iter.next();
		//			out.println("Post ID: " + curr.getId());
		//		}

		//		Facebook fac = new FacebookFactory().getInstance();
		//		fac.setOAuthAppId(Constants.FACEBOOK_APP_ID, Constants.FACEBOOK_APP_KEY);
		//		//facebook.setOAuthPermissions(commaSeparetedPermissions);
		//		fac.setOAuthAccessToken(new AccessToken(Constants.FACEBOOK_ACCESS_TOKEN, null));
		//		out.println("HERE");
		//		
		//		try {
		//			Page renzi = fac.getPage("113335124914");
		//			out.println("Likes: " + renzi.getLikes() + "<br>");
		//			out.println("Talking About: " + renzi.getTalkingAboutCount() + "<br>");
		//			out.println("Created Time: " + renzi.getCreatedTime() + "<br>");
		//			out.println("Checkins: " + renzi.getCheckins() + "<br>");
		//			out.println("Were Here Count: " + renzi.getWereHereCount() + "<br>");
		//			out.println("Location: " + renzi.getLocation() + "<br>");
		//		} catch (FacebookException e) {
		//			e.printStackTrace();
		//		}


		////		ArrayList<String> input = new ArrayList<String>();
		////		input.add("ignaziomarinotv");
		////		input.add("NicolaOttaviani");
		////		input.add("pisapiaXmilano");
		////		input.add("delbonoemilio");
		////		input.add("simoneuggetti");
		////		input.add("FassinoSindaco");
		////		input.add("ritarossasindaco");
		////		input.add("borgnasindaco");
		////		input.add("FabrizioBrignolo");
		////		input.add("AndreaBallareSindaco");
		////		input.add("videodemagistris");
		////		input.add("Mr21061983");
		////		input.add("faustopepe");
		////		input.add("piodelgaudiosindaco");
		////		input.add("cialentemassimo");
		////		input.add("merola2011");
		////		input.add("TheModenaperpighi");
		////		input.add("fabriziomatteucci");
		////		input.add("sindacobalzani");
		////		input.add("andreagnassi");
		////		input.add("TvPaoloPerrone");
		////		input.add("BZforSpagnolli");
		////		input.add("adducesocial");
		////		input.add("CosoliniSindaco");
		////		input.add("Romoli2012");
		////		input.add("claudiopedrotti");
		////		input.add("lceriscioli");
		////		input.add("nellabrambatti");
		////		input.add("GANAUSINDACO");
		////		input.add("MarioOcchiuto");
		////		input.add("michelecampisi");
		////		input.add("DamianoSindacoTP");
		////		input.add("marcodoriachannel");
		////		input.add("FedericiMassimo");
		////		input.add("Marcofilippeschi1");
		////		input.add("AlessandroTambellini");
		////		input.add("Bonifaziemilio");
		////		input.add("AchilleVariati");
		////		
		////		Iterator<String> iter = input.iterator();
		////		while (iter.hasNext()) {
		////			String curr = iter.next();
		////			out.println(curr + ";" + YouTubeUtils.getUserId(curr) + "<br>");
		////		}
		// 		
		//		out.println("<br>");
		//		out.println("<br>");
		//		out.println("<br>");
		//		out.println("<br>");
		//		Date f = null;
		//		Date t = null;
		//		try {
		//
		////			f = DateUtils.parseDateAndTime((String) "01-09-2012 00:00:00");
		////			t = DateUtils.parseDateAndTime((String) "16-09-2013 00:00:00");
		//			f = DateUtils.parseDateAndTime(req.getParameter("from"));
		//			t = DateUtils.parseDateAndTime(req.getParameter("to"));
		//		} catch (ParseException e) {
		//			e.printStackTrace();
		//		}
		//		
		//		out.println(YouTubeUtils.getUserId("matteorenzi"));
		//		out.println("<br>");
		//		
		////		Hashtable<String, String> baseInfo = YouTubeUtils.getBaseInfo(req.getParameter("id1"));
		////		out.println("subscribers " + baseInfo.get("subscribers") + "<br>");
		////		out.println("views " + baseInfo.get("views") + "<br>");
		////		out.println("joineddate " + baseInfo.get("joineddate") + "<br>");
		////		out.println("baseInfo " + baseInfo.toString());
		////		
		////		out.println("<br>");
		////		
		////		List<Activity> act = YouTubeUtils.getActivities(req.getParameter("id2"), f, t);
		////		out.println("videos " + act.size() + "<br>");
		////		Hashtable<String, BigInteger> res = YouTubeUtils.getAllUserInteraction(act);
		////		out.println("viewcount " + res.get("viewcount") + "<br>");
		////		out.println("likecount " + res.get("likecount") + "<br>");
		////		out.println("dislikecount " + res.get("dislikecount") + "<br>");
		////		out.println("favouritecount " + res.get("favouritecount") + "<br>");
		////		out.println("commentcount " + res.get("commentcount") + "<br>");
		////		out.println("res " + res.toString());
		//		
		//		
		//		YouTube youtube = new YouTube.Builder(new UrlFetchTransport(), new JacksonFactory(), null).setApplicationName("TEST").setGoogleClientRequestInitializer(new YouTubeRequestInitializer(Constants.YOUTUBE_API_KEY)).build();
		//		YouTube.Activities.List activityRequest = youtube.activities().list("id,snippet,contentDetails");
		//		activityRequest.setChannelId(req.getParameter("id1"));
		//		activityRequest.setFields("items(id,snippet,contentDetails),nextPageToken");
		//		activityRequest.setMaxResults(10L);
		//		activityRequest.setPublishedAfter(new DateTime(f));
		//		activityRequest.setPublishedBefore(new DateTime(t));
		//		ActivityListResponse activities = activityRequest.execute();
		//		List<Activity> activityList = activities.getItems();
		//		
		//		while (activityList != null && activityList.size() > 0) {
		//			for (Activity a : activityList) {
		//				out.println(a.getId() + "<br>");
		//			}
		//			String nextToken = activities.getNextPageToken();
		//			out.println("nextToken: " + nextToken + "<br>");
		//			if (nextToken == null) {
		//				break;
		//			}
		////			activityRequest = getYT().activities().list("id,snippet,contentDetails").setChannelId(userId).setFields("items(id,snippet,contentDetails)").setPublishedAfter(fr).setPublishedBefore(t).setPageToken(activities.getNextPageToken());
		//			activityRequest.setPageToken(nextToken);
		//			activities = activityRequest.execute();
		//			activityList = activities.getItems();
		//		}



		//		ArrayList<Graph> result = new ArrayList<Graph>();
		//
		//		Date f = null;
		//		Date t = null;
		//		try {
		//
		//			f = DateUtils.parseDateAndTime((String) "01-09-2013 00:00:00");
		//			t = DateUtils.parseDateAndTime((String) "16-09-2013 00:00:00");
		//
		//
		//			// Get the Datastore Service
		//			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		//			Query q;
		//			if (f != null && t != null) {
		//				Filter fromFilter = new FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, f);
		//				Filter toFilter = new FilterPredicate("date", FilterOperator.LESS_THAN_OR_EQUAL, t);
		//				Filter transmissionFilter = new FilterPredicate("idFacebook", FilterOperator.EQUAL, (String) "113335124914");
		//				Filter fromToTransmissionFilter = CompositeFilterOperator.and(transmissionFilter, fromFilter, toFilter);
		//				q = new Query(Constants.FACEBOOK_MONITOR_TABLE).setFilter(fromToTransmissionFilter).addSort("date", SortDirection.ASCENDING);
		//			} else {
		//				q = new Query(Constants.FACEBOOK_MONITOR_TABLE).addSort("date", SortDirection.ASCENDING);
		//			}
		//
		//
		//			// Use PreparedQuery interface to retrieve results
		//			PreparedQuery pq = datastore.prepare(q);
		//			Long previousOrdinate = 0L;
		//			for (Entity ent : pq.asIterable()) {
		//				String axisReturned = "";
		//				Long ordinateReturned = 0L;
		//
		//				Object datastoreAxis = ent.getProperty((String) "date");
		//				if (datastoreAxis instanceof Date) {
		//					axisReturned = DateUtils.formatDateAndTime((Date) datastoreAxis);
		//				} else {
		//					axisReturned = datastoreAxis.toString();
		//				}
		//
		//				Object datastoreOrdinate = ent.getProperty((String) "like_count");
		//				if (datastoreOrdinate instanceof Long) {
		//					ordinateReturned = (Long) datastoreOrdinate;
		//					if (ordinateReturned.compareTo(0L) == 0) {
		//						ordinateReturned = previousOrdinate;
		//						out.println("ordinateReturned is 0 previous is " + ordinateReturned);
		//					} else {
		//						previousOrdinate = ordinateReturned;
		//						out.println("ordinateReturned is " + ordinateReturned);
		//					}
		//				} else {
		//					out.println("ordinateReturned is " + ordinateReturned + " previous is " + previousOrdinate);
		//					ordinateReturned = previousOrdinate;
		//				}
		//
		//				Graph gd = new Graph(axisReturned, ordinateReturned);
		//				result.add(gd);
		//				out.println("Coordinate: " + gd.toString() + "<br>");
		//
		//			} 
		//		} catch (ParseException e) {
		//			e.printStackTrace();
		//		}



		//		String dateFrom = req.getParameter("from");
		//		String dateTo = req.getParameter("to");
		//		Date f = null;
		//		Date t = null;
		//		try {
		////			f = DateUtils.parseDateAndTime("01-09-2013 00:00:00");
		////			t = DateUtils.addDayToDate(f, 1);
		//			f = DateUtils.parseDateAndTime(dateFrom);
		//			t = DateUtils.parseDateAndTime(dateTo);
		//		} catch (ParseException e) {
		//			e.printStackTrace();
		//		}
		//		ArrayList<Post> posts = FacebookUtils.getAllPosts("113335124914", f, t, null);
		//		Iterator<Post> iterPost = posts.iterator();
		//		while (iterPost.hasNext()) {
		//			Post currPost = iterPost.next();
		//			out.println("Post ID: " + currPost.getId() + "<br>");
		//			out.println("Post Type: " + currPost.getType() + "<br>");
		//			out.println("Post From: " + currPost.getFrom().getName() + "<br>");
		//			out.println("Message: " + currPost.getMessage() + "<br>");
		//			
		//			List<Tag> tags = currPost.getMessageTags();
		//			if (tags != null) {
		//				Iterator<Tag> iterTags = tags.iterator();
		//				while (iterTags.hasNext()) {
		//					Tag currTag = iterTags.next();
		//					out.println("&nbsp;Tag Name: " + currTag.getName() + "<br>");
		//					out.println("&nbsp;Tag Type: " + currTag.getType() + "<br>");
		//					out.println("&nbsp;Tag Metadata: " + currTag.getMetadata() + "<br>");
		//				}
		//			}
		//			
		//			List<IdNameEntity> tos = currPost.getTo();
		//			if (tos != null) {
		//				Iterator<IdNameEntity> iterTo = tos.iterator();
		//				while (iterTo.hasNext()) {
		//					IdNameEntity currTo = iterTo.next();
		//					out.println("&nbsp;To Id: " + currTo.getId() + "<br>");
		//					out.println("&nbsp;To Name: " + currTo.getName() + "<br>");
		//				}
		//			}
		//			
		//			List<Property> props = currPost.getProperties();
		//			if (props != null) {
		//				Iterator<Property> iterProps = props.iterator();
		//				while (iterProps.hasNext()) {
		//					Property currProp = iterProps.next();
		//					out.println("&nbsp;Prop name: " + currProp.getName() + "<br>");
		//					out.println("&nbsp;Prop Text: " + currProp.getText() + "<br>");
		//				}
		//			}
		//			
		//			
		//			ArrayList<Comment> comms = FacebookUtils.getAllComments(currPost);
		//			Iterator<Comment> iterComms = comms.iterator();
		//			while (iterComms.hasNext()) {
		//				Comment currComm = iterComms.next();
		//				out.println("&nbsp;&nbsp;Comment ID: " + currComm.getId() + "<br>");
		//				out.println("&nbsp;&nbsp;Comment From: " + currComm.getFrom().getName() + "<br>");
		//				out.println("&nbsp;&nbsp;Comment Message: " + currComm.getMessage() + "<br>");
		//				out.println("&nbsp;&nbsp;Comment Metadata: " + currComm.getMetadata() + "<br>");
		//			}
		//		}
		//		




		//		String mail = "";
		//		
		////		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		////		Query q;
		////		PreparedQuery pq;
		////		Filter idFilter = new FilterPredicate("idTransaction", FilterOperator.EQUAL, "7182af0116b073f5299372a6e5be3c59");
		////		q = new Query("task").setFilter(idFilter);
		////		pq = datastore.prepare(q);
		////		int executedTask = pq.countEntities();
		////		out.println("AAAAAAA: " + executedTask);
		////		mail = mail + "ExTask: " + executedTask + "\n";
		//		
		//		
		//		Date f = null;
		//		Date t = null;
		//		try {
		//			f = DateUtils.parseDateAndTime("01-08-2013 00:00:00");
		//			t = DateUtils.parseDateAndTime("03-08-2013 00:00:00");
		//		} catch (ParseException e) {
		//			e.printStackTrace();
		//		}
		//
		//
		//		//Get all Post
		//		ArrayList<Post> posts = FacebookUtils.getAllPosts("113335124914", f, t, null);
		//
		//		ArrayList<Post> postFromPage = new ArrayList<Post>();
		//		ArrayList<Post> postFromFan = new ArrayList<Post>();
		//
		//		Iterator<Post> iterPost = posts.iterator();
		//		while (iterPost.hasNext()) {
		//			Post currPost = iterPost.next();
		//			if (currPost.getFrom().getId().equals("113335124914")) {
		//				postFromPage.add(currPost);
		//			} else {
		//				postFromFan.add(currPost);
		//			}
		//		}
		//
		//		//Get all Comments
		//		ArrayList<Comment> comments = FacebookUtils.getComments(postFromPage);		
		//
		//		//Get all Unique Authors of Comments
		//		ArrayList<String> uniqueAuth = FacebookUtils.getUniqueAuthors(comments);
		//		out.println("SIZE: " + uniqueAuth.size());
		//		mail = mail + "SIZE1: " + uniqueAuth.size() + "\n";
		////		Iterator<String> iter = uniqueAuth.iterator();
		////		while (iter.hasNext()) {
		////			out.println(iter.next() + "<br>");
		////		}
		//		
		//		
		//		Date f1 = null;
		//		Date t1 = null;
		//		try {
		//			f1 = DateUtils.parseDateAndTime("04-08-2013 00:00:00");
		//			t1 = DateUtils.parseDateAndTime("07-08-2013 00:00:00");
		//		} catch (ParseException e) {
		//			e.printStackTrace();
		//		}
		//
		//
		//		//Get all Post
		//		ArrayList<Post> posts1 = FacebookUtils.getAllPosts("113335124914", f1, t1, null);
		//
		//		ArrayList<Post> postFromPage1 = new ArrayList<Post>();
		//		ArrayList<Post> postFromFan1 = new ArrayList<Post>();
		//
		//		Iterator<Post> iterPost1 = posts1.iterator();
		//		while (iterPost1.hasNext()) {
		//			Post currPost = iterPost1.next();
		//			if (currPost.getFrom().getId().equals("113335124914")) {
		//				postFromPage1.add(currPost);
		//			} else {
		//				postFromFan1.add(currPost);
		//			}
		//		}
		//
		//		//Get all Comments
		//		ArrayList<Comment> comments1 = FacebookUtils.getComments(postFromPage1);		
		//
		//		//Get all Unique Authors of Comments
		//		ArrayList<String> uniqueAuth1 = FacebookUtils.getUniqueAuthors(comments1);
		//		out.println("SIZE: " + uniqueAuth1.size());
		//		mail = mail + "SIZE2: " + uniqueAuth1.size() + "\n";
		//		
		//		
		//		uniqueAuth.addAll(uniqueAuth1);
		//		out.println("SIZE: " + uniqueAuth.size());
		//		mail = mail + "SIZE3: " + uniqueAuth.size() + "\n";
		//		MailUtils.sendMail("paserv%40gmail.com", "Ciao", mail, "al", "dd");

	}


	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}



}
