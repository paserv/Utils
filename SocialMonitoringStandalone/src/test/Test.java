package test;

import it.osg.utils.FacebookUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import facebook4j.FacebookException;
import facebook4j.Post;

public class Test {


	public static void main(String[] args) throws FacebookException {
		
		Post completePost = FacebookUtils.getPost("1377711462_10201907339029933");
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("https://1-dot-01-monitorfacebookpages.appspot.com/_ah/api/datastore/v1/registerEntity/paolo/servillo/aaa");
//		List nameValuePairs = new ArrayList(3);
//		nameValuePairs.add(new BasicNameValuePair("id", "paolo"));
//		nameValuePairs.add(new BasicNameValuePair("name", "servillo"));
//		nameValuePairs.add(new BasicNameValuePair("session", "aaa"));
		
		try {
//			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}
