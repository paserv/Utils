package it.osg.utils;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class DatastoreServiceUtil {

	private static String rootUrl = "https://1-dot-01-monitorfacebookpages.appspot.com/_ah/api/datastore/v1/";
	
	public static void registerEntity(String name, String id, String session) {

		String url = rootUrl + "registerEntity/" + id + "/" +  name + "/" +  session;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		
			HttpResponse response;
			try {
				response = client.execute(post);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void saveProperty (String id, String session, String propName, long propvalue) {
		String url = rootUrl + "saveProperty/" + id + "/" +  session + "/" +  propName + "/" + propvalue;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		
			HttpResponse response;
			try {
				response = client.execute(post);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
	}
	
}
