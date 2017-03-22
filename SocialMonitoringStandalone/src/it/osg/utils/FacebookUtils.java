package it.osg.utils;

import it.osg.data.RequestCounter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import facebook4j.Comment;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Like;
import facebook4j.PagableList;
import facebook4j.Paging;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;

public class FacebookUtils {

	private static ArrayList<Facebook> facebookPool = null;
	private static int nextFBCredential = 0;
	private static RequestCounter reqCounter = new RequestCounter();

//	private static final long SLIDING_WINDOW = 6000L;
//	private static final int MAX_REQUEST = 600;
//	private static final long THROTTLING_SLEEP = 10000L;

	private static Logger LOGGER = Logger.getLogger(FacebookUtils.class.getName());

	public static void main(String[] args) throws FacebookException {
		
		System.getProperties().put("http.proxyHost", "proxy.gss.rete.poste");
		System.getProperties().put("http.proxyPort", "8080");
		System.getProperties().put("http.proxyUser", "rete\\servill7");
		System.getProperties().put("http.proxyPassword", "Paolos11");
		
		Date f = null;
		Date t = null;
		try {
			f = DateUtils.parseDateAndTime("01-01-2014 00:00:00");
			t = DateUtils.parseDateAndTime("04-01-2014 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ArrayList<Post> posts = FacebookUtils.getAllPosts("179618821150", f, t, null);
		for (Post curr : posts) {
			FacebookUtils.getAllComments(curr);
		}
	}

	public static synchronized Facebook getFB() {
		if (facebookPool != null) {
			if ((nextFBCredential + 1) == Constants.FACEBOOK_CREDENTIAL_VECTOR.size()) {
				nextFBCredential = 0;
			} else {
				nextFBCredential++;
			}
			return facebookPool.get(nextFBCredential);
		}

		facebookPool = new ArrayList<Facebook>();
		for (int i = 0; i < Constants.FACEBOOK_CREDENTIAL_VECTOR.size(); i++) {
			Facebook fac = new FacebookFactory().getInstance();
			fac.setOAuthAppId(Constants.FACEBOOK_CREDENTIAL_VECTOR.get(i).getAppId(), Constants.FACEBOOK_CREDENTIAL_VECTOR.get(i).getAppKey());
			//facebook.setOAuthPermissions(commaSeparetedPermissions);
			fac.setOAuthAccessToken(new AccessToken(Constants.FACEBOOK_CREDENTIAL_VECTOR.get(i).getAppAccessToken(), null));
			facebookPool.add(fac);
		}
		return facebookPool.get(nextFBCredential);
	}



	public static Hashtable<String, Hashtable<String, Post>> getPostByKeyword (String keyword, String since, String until) throws FacebookException {

		Date from = null;
		Date to = null;
		try {
			from = DateUtils.parseDateAndTime(since);
			to = DateUtils.parseDateAndTime(until);
		} catch (ParseException e) {
			e.printStackTrace();
		}


		Hashtable<String, Hashtable<String, Post>> result = new Hashtable<String, Hashtable<String, Post>>();

		ArrayList<Post> appoggio = new ArrayList<Post>();

		Facebook facebook = getFB();
		ResponseList<Post> postsByKey;
//		try {
			checkThrottling();
			postsByKey = facebook.searchPosts(keyword, new Reading().since(from).until(to));
			appoggio.addAll(postsByKey);

			//Fetching Post
			checkThrottling();
			Paging<Post> pagingPost = postsByKey.getPaging();

			while (true) {
				if (pagingPost != null) {
					ResponseList<Post> nextPosts;
					checkThrottling();
					nextPosts = facebook.fetchNext(pagingPost);
					appoggio.addAll(nextPosts);
					checkThrottling();
					pagingPost = nextPosts.getPaging();

				} else {
					break;
				}
			}

//		} catch (FacebookException e1) {
//			e1.printStackTrace();
//			LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
//		}


		Iterator<Post> iterAppoggio = appoggio.iterator();
		while(iterAppoggio.hasNext()) {
			Post curr = iterAppoggio.next();
			if (result.containsKey(curr.getFrom().getId())) {
				result.get(curr.getFrom().getId()).put(curr.getId(), curr);
			} else {
				Hashtable<String, Post> newSet = new Hashtable<String, Post>();
				newSet.put(curr.getId(), curr);
				result.put(curr.getFrom().getId(), newSet);
			}
		}

		return result;
	}

	//	public static Hashtable<String, String> getFeedByKeyword (String keyword, String since, String until) {
	//
	//		int sizeVectorAccessToken = Constants.FACEBOOK_CREDENTIAL_VECTOR.size();
	//		Random rnd = new Random();
	//		int randomIndex = rnd.nextInt(sizeVectorAccessToken);
	//		String access_token = Constants.FACEBOOK_CREDENTIAL_VECTOR.get(randomIndex).getAppAccessToken();
	//
	//		Hashtable<String, String> result = new Hashtable<String, String>();
	//
	//		String url = Constants.FACEBOOK_GRAPH_API_ROOT_URL + "search?q=" + keyword + "&since=" + since + "&until=" + until + "&access_token=" + access_token;
	//
	//		String jsonString = JSONObjectUtil.retrieveJson(url);
	//
	//		Object objJson = JSONValue.parse(jsonString);
	//		JSONObject json =(JSONObject) objJson;
	//		JSONArray arrayResult = (JSONArray) json.get("data");
	//		JSONArray paging = (JSONArray) json.get("paging");
	//
	//		Iterator<JSONObject> iter = arrayResult.iterator();
	//		while (iter.hasNext()) {
	//			JSONObject current = iter.next();
	//			String currentIdFeed = (String) current.get("id");
	//			JSONObject currentFrom = (JSONObject) current.get("from");
	//			String currentIdAuthor = (String) currentFrom.get("id");
	//			result.put(currentIdFeed, currentIdAuthor);
	//		}
	//
	//
	//
	//		return result;
	//	}

	public static Date getActivityInterval (String pageId) throws FacebookException{

		Facebook facebook = getFB();
		Date f = null;
		Date t = null;
		try {
			f = DateUtils.parseDateAndTime("01-02-2004 00:00:00");
			t = DateUtils.addMonthToDate(f, 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}

//		try {
			while (true) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				checkThrottling();
				ResponseList<Post> facResults = facebook.getFeed(pageId, new Reading().since(f).until(t).fields("created_time").limit(1));
				if (facResults != null && facResults.size() != 0) {
					Post currPost = facResults.get(0);
					return currPost.getCreatedTime();
				} else {
					f = t;
					t = DateUtils.addMonthToDate(f, 1);
				}
			}

//		} catch (FacebookException e) {
//			e.printStackTrace();
//			LOGGER.log(Level.SEVERE, e.getMessage(), e);
//		}
//		return null;

	}



	public static Hashtable<String, Object> getBaseInfoFromJson (String pageId) {

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		checkThrottling();
		Hashtable<String, Object> result = new Hashtable<String, Object>();
		String jsonString = JSONObjectUtil.retrieveJson(Constants.FACEBOOK_GRAPH_API_ROOT_URL + pageId);
		//		DatastoreUtils.addRow("test", "json", new Text(jsonString));

		Object objJson = JSONValue.parse(jsonString);
		JSONObject json =(JSONObject) objJson;

		String likeCount = JSONObjectUtil.retrieveJsonPath(json, "likes");
		String talkingAboutCount = JSONObjectUtil.retrieveJsonPath(json, "talking_about_count");
		String pageName = JSONObjectUtil.retrieveJsonPath(json, "name");
		//TODO retrieve anche degli altri campi

		result.put("likes", likeCount);
		result.put("talking_about_count", talkingAboutCount);
		result.put("name", pageName);

		return result;
	}


	public static ArrayList<Post> getAllOwnPosts (String pageId, Date f, Date t, String[] campi) throws FacebookException {
		ArrayList<Post> result = new ArrayList<Post>();
		ArrayList<Post> posts = getAllPosts(pageId, f, t, campi);
		Iterator<Post> iter = posts.iterator();
		while (iter.hasNext()) {
			Post currPost = iter.next();
			if (currPost.getFrom().getId().toString().equalsIgnoreCase(pageId)){
				result.add(currPost);
			}
		}
		return result;
	}

	public static Post getPost (String postId) throws FacebookException {
		Facebook facebook = getFB();
		Post result = null;
//		try {
			checkThrottling();
			result = facebook.getPost(postId);
//		} catch (FacebookException e) {
//			e.printStackTrace();
//			LOGGER.log(Level.SEVERE, e.getMessage(), e);
//		}
		return result;
	}

	public static ArrayList<Post> getAllPosts (String pageId, Date f, Date t, String[] campi) throws FacebookException {

		ArrayList<Post> result = new ArrayList<Post>();

		Facebook facebook = getFB();	

		ResponseList<Post> facResults;
		checkThrottling();
//		try {
			if (campi != null) {
				facResults = facebook.getFeed(pageId, new Reading().since(f).until(t).fields(campi));
			} else {
				facResults = facebook.getFeed(pageId, new Reading().since(f).until(t));
			}

			result.addAll(facResults);

			//			//Fetching Post
			checkThrottling();
			Paging<Post> pagingPost = facResults.getPaging();
			while (true) {
				if (pagingPost != null) {
					checkThrottling();
					ResponseList<Post> nextPosts = facebook.fetchNext(pagingPost);
					if (nextPosts != null && nextPosts.size() > 0) {
						Post firstPost = nextPosts.get(0);
						if (firstPost.getCreatedTime().after(t) || firstPost.getCreatedTime().before(f)) {
							break;
						}
						Iterator<Post> itr = nextPosts.iterator();
						while (itr.hasNext()) {
							Post fetchPost = itr.next();
							if (fetchPost.getCreatedTime().after(f) && fetchPost.getCreatedTime().before(t)) {
								result.add(fetchPost);
							}
						}
					} else {
						break;
					}
					checkThrottling();
					pagingPost = nextPosts.getPaging();
				} else {
					break;
				}
			}
//		} catch (FacebookException e) {
//			e.printStackTrace();
//			LOGGER.log(Level.SEVERE, e.getMessage(), e);
//		}

		return result;

	} 

	public static ArrayList<Comment> getAllComments(Post curr) throws FacebookException {
		ArrayList<Comment> result = new ArrayList<Comment>();
		Facebook facebook = getFB();

		checkThrottling();
		PagableList<Comment> comments = curr.getComments();

		if (comments != null) {
			Iterator<Comment> iterComment = comments.iterator();
			//Salvo i Commenti
			while (iterComment.hasNext()) {
				Comment currComment = iterComment.next();
				if (currComment != null) {
					result.add(currComment);
				}
				//TODO salvare le risposte ai commenti

			}
			//Itero sulla paginazione per salvare tutti i commenti
			checkThrottling();
			Paging<Comment> paging = comments.getPaging();
			while (true) {
				if (paging != null) {
					ResponseList<Comment> nextPage;
//					try {
						checkThrottling();
						nextPage = facebook.fetchNext(paging);
						if (nextPage != null) {
							Iterator<Comment> itr = nextPage.iterator();
							while (itr.hasNext()) {
								Comment cmt = itr.next();
								if (cmt != null) {
									result.add(cmt);
								}
							}
						} else {
							break;
						}
						checkThrottling();
						paging = nextPage.getPaging();
//					} catch (FacebookException e) {
//						e.printStackTrace();
//						LOGGER.log(Level.SEVERE, e.getMessage(), e);
//					}
				} else {
					break;
				}

			}

		}

		return result;
	}

	public static ArrayList<Like> getAllLikes (Post post) throws FacebookException {
		ArrayList<Like> result = new ArrayList<Like>();
		Facebook facebook = getFB();
		ResponseList<Like> likes;
//		try {
			checkThrottling();
			likes = facebook.getPostLikes(post.getId());
			if (likes != null)  {
				result.addAll(likes);
			}
			checkThrottling();
			Paging<Like> paging = likes.getPaging();
			while (true) {
				if (paging != null) {
					ResponseList<Like> nextPage;
//					try {
						checkThrottling();
						nextPage = facebook.fetchNext(paging);
						if (nextPage != null) {
							Iterator<Like> itr = nextPage.iterator();
							while (itr.hasNext()) {
								Like cmt = itr.next();
								if (cmt != null) {
									result.add(cmt);
								}
							}
						} else {
							break;
						}
						checkThrottling();
						paging = nextPage.getPaging();
//					} catch (FacebookException e) {
//						e.printStackTrace();
//						LOGGER.log(Level.SEVERE, e.getMessage(), e);
//					}
				} else {
					break;
				}

			}

//		} catch (FacebookException e) {
//			e.printStackTrace();
//			LOGGER.log(Level.SEVERE, e.getMessage(), e);
//		}

		return result;

	}

	public static ArrayList<Comment> getComments (ArrayList<Post> posts) throws FacebookException {
		ArrayList<Comment> result = new ArrayList<Comment>();
		Iterator<Post> iterPost = posts.iterator();
		while (iterPost.hasNext()) {
			Post curPost = iterPost.next();
			ArrayList<Comment> currComments = getAllComments(curPost);
			result.addAll(currComments);
		}
		return result;

	}


	public static ArrayList<Like> getLikes (ArrayList<Post> posts) throws FacebookException {
		ArrayList<Like> result = new ArrayList<Like>();
		Iterator<Post> iterPost = posts.iterator();
		while (iterPost.hasNext()) {
			Post curPost = iterPost.next();
			ArrayList<Like> currLikes = getAllLikes(curPost);
			result.addAll(currLikes);
		}
		return result;

	}

	public static long getShares (ArrayList<Post> posts) {
		long result = 0;
		Iterator<Post> iterPost = posts.iterator();
		while (iterPost.hasNext()) {
			Post curPost = iterPost.next();
			if (curPost.getSharesCount() != null) {
				result = result + curPost.getSharesCount();
			}
		}
		return result;

	}

	public static int getSharesInteger (ArrayList<Post> posts) {
		int result = 0;
		Iterator<Post> iterPost = posts.iterator();
		while (iterPost.hasNext()) {
			Post curPost = iterPost.next();
			if (curPost.getSharesCount() != null) {
				result = result + curPost.getSharesCount();
			}
		}
		return result;

	}

	public static int getSharesInteger (Post post) {
		if (post.getSharesCount() != null) return post.getSharesCount();
		return 0;

	}

	public static double getCommentLike (ArrayList<Comment> comments) {
		double result = 0;
		Iterator<Comment> iterComm = comments.iterator();
		while (iterComm.hasNext()) {
			Comment curComm = iterComm.next();
			result = result + curComm.getLikeCount();
		}
		return result;

	}

	public static ArrayList<String> getUniqueAuthors (ArrayList<Comment> comments) {

		ArrayList<String> result = new ArrayList<String>();

		//Per tutti i commenti accumulati prelevo gli autori
		Iterator<Comment> iterComm = comments.iterator();
		while (iterComm.hasNext()) {
			Comment currComm = iterComm.next();
			result.add(currComm.getFrom().getId());
		}

		return ArrayUtils.removeDuplicate(result);
	}

	public static long getCommentsFromIdCount (String id, ArrayList<Comment> comments) {

		long result = 0;

		//Per tutti i commenti accumulati prelevo gli autori
		Iterator<Comment> iterComm = comments.iterator();
		while (iterComm.hasNext()) {
			Comment currComm = iterComm.next();
			if (currComm.getFrom().getId().equalsIgnoreCase(id)) {
				result++;
			}

		}

		return result;
	}



	public static int getCommentsFromIdCountInteger (String id, ArrayList<Comment> comments) {

		int result = 0;

		//Per tutti i commenti accumulati prelevo gli autori
		Iterator<Comment> iterComm = comments.iterator();
		while (iterComm.hasNext()) {
			Comment currComm = iterComm.next();
			if (currComm.getFrom().getId().equalsIgnoreCase(id)) {
				result++;
			}

		}

		return result;
	}


	public static ArrayList<Hashtable<String, Object>> likeTalkAnalysis(String jsonString) {
		ArrayList<Hashtable<String, Object>> result = new ArrayList<Hashtable<String, Object>>();
		Object objJson = JSONValue.parse(jsonString);
		JSONObject json =(JSONObject) objJson;

		Hashtable<String, Object> currRow = new Hashtable<String, Object>();

		String likeCount = JSONObjectUtil.retrieveJsonPath(json, "likes");
		String talkingAboutCount = JSONObjectUtil.retrieveJsonPath(json, "talking_about_count");

		//		System.out.println(likeCount + " " + talkingAboutCount);

		if (!likeCount.equalsIgnoreCase("")) {
			currRow.put("like_count", Integer.parseInt(likeCount));
		}

		if (!talkingAboutCount.equalsIgnoreCase("")) {
			currRow.put("talking_about_count", Integer.parseInt(talkingAboutCount));
		}

		Calendar cal= Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(Calendar.HOUR_OF_DAY, 2);
		Date cestDate= cal.getTime();

		currRow.put("timestamp", cal.getTimeInMillis());
		currRow.put("date", cestDate);

		//System.out.println(cal.getTimeInMillis() + " " + sdf.format(cestDate));

		result.add(currRow);

		return result;
	}

	public static ArrayList<Hashtable<String, String>> analyse(String jsonString) {
		ArrayList<Hashtable<String, String>> result = new ArrayList<Hashtable<String, String>>();
		Object objJson = JSONValue.parse(jsonString);
		JSONObject json =(JSONObject) objJson;
		JSONArray dataArray = (JSONArray) json.get("data");
		if (dataArray != null){
			for (int i = 0; i < dataArray.size(); i++) {
				Hashtable<String, String> currRow = new Hashtable<String, String>();
				currRow.put("timestamp", String.valueOf(System.currentTimeMillis()));
				JSONObject currJson =(JSONObject) dataArray.get(i);
				String postId = JSONObjectUtil.retrieveJsonPath(currJson, "id");
				currRow.put("postId", postId);
				String object_id = JSONObjectUtil.retrieveJsonPath(currJson, "object_id");
				currRow.put("postId", object_id);
				String created_time = JSONObjectUtil.retrieveJsonPath(currJson, "created_time");
				currRow.put("created_time", created_time);
				String updated_time = JSONObjectUtil.retrieveJsonPath(currJson, "updated_time");
				currRow.put("updated_time", updated_time);
				JSONObject currComments = (JSONObject) currJson.get("comments");
				if (currComments != null){
					JSONArray currCommentsArray = (JSONArray) currComments.get("data");
					String comment_number = String.valueOf(currCommentsArray.size());
					currRow.put("comment_number", comment_number);
					int post_likes = Integer.parseInt(JSONObjectUtil.retrieveJsonPath(currJson, "likes/count"));
					currRow.put("post_likes", String.valueOf(post_likes));
					int comments_like = 0;
					for (int j = 0; j < currCommentsArray.size(); j++) {
						JSONObject currJsonComment =(JSONObject) currCommentsArray.get(j);
						String like_count = JSONObjectUtil.retrieveJsonPath(currJsonComment, "like_count");
						int currCommLike = Integer.parseInt(like_count);
						comments_like = comments_like + currCommLike;
					}
					String lik = String.valueOf(comments_like);
					currRow.put("comments_like", lik);
				}
				result.add(currRow);
			}

		}


		return result;
	}

	public static RequestCounter getReqCounter() {
		return reqCounter;
	}

	public static void setReqCounter(RequestCounter reqCounter) {
		FacebookUtils.reqCounter = reqCounter;
	}

	public static void checkThrottling() {
		while (true) {
			long currentTime = System.currentTimeMillis();
			long sixhundredSecondsAgo = currentTime - Configuration.SLIDING_WINDOW;
			long requests = reqCounter.greaterThanNumber(sixhundredSecondsAgo);

			LOGGER.fine("Current requests in sliding window: " + requests);
			if (requests < Configuration.MAX_REQUEST) {
				reqCounter.addRequest(currentTime);
				break;
			} else {
				LOGGER.warning(requests + " requests in the last " + Configuration.SLIDING_WINDOW/1000 + " seconds");
				LOGGER.warning("Too much requests, throttling risk: Sleeping");
				try {
					Thread.sleep(Configuration.THROTTLING_SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}


}
