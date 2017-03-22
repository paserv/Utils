package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class GetFBAccessToken {

	public static void main(String[] args) {
		HttpClient client = HttpClientBuilder.create().build();
		String appId = "236482829881966";
		String appKey = "92756a476976440568287e68627a21a0";
		HttpGet request = new HttpGet("https://graph.facebook.com/oauth/access_token?type=client_cred&client_id=" + appId + "&client_secret=" + appKey);
		HttpResponse response;
		try {
			response = client.execute(request);
			// Get the response
			BufferedReader rd = new BufferedReader
			  (new InputStreamReader(response.getEntity().getContent()));
			    
			String line = "";
			while ((line = rd.readLine()) != null) {
			  System.out.println(line);
			} 
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
}
