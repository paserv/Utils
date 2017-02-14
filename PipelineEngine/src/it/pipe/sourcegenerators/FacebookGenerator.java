package it.pipe.sourcegenerators;

import it.pipe.core.SourceGenerator;

import java.util.ArrayList;
import java.util.Iterator;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;

public class FacebookGenerator extends SourceGenerator {

	public FacebookGenerator() {
		super();
	}
	
	public FacebookGenerator(String modName, String conFilePath) {
		super(modName, conFilePath);
		System.setProperty("java.net.useSystemProxies", "true");
	}

	@Override
	public ArrayList<String> add() {
		Facebook facebook = new FacebookFactory().getInstance();
		try {
			ResponseList<Post> results = facebook.searchPosts("watermelon");
		} catch (FacebookException e) {
			e.printStackTrace();
		}
		System.out.println("ciao");
		return null;
	}
	
	public static void main(String[] args) {
		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId("156346967866710", "e0f880cc248e811c98952d9a44a27ce4");
//		AccessToken accessToken = new AccessToken("156346967866710|gnswdSXw_ObP0RaWj5qqgK_HtCk");
//		facebook.setOAuthAccessToken(accessToken);
		facebook.setOAuthAccessToken(new AccessToken("156346967866710%7CgnswdSXw_ObP0RaWj5qqgK_HtCk", null));
		try {
//			ResponseList<Post> results = facebook.getFeed(new Reading().fields("message"));
			ResponseList<Post> results = facebook.getFeed();
			Iterator<Post> iter = results.iterator();
			while (iter.hasNext()) {
				Post curr = iter.next();
				System.out.println(curr.getMessage());
			}
		} catch (FacebookException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("ciao");
	}

}
