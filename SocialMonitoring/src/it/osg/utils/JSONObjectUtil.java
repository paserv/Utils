package it.osg.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.simple.JSONObject;

public class JSONObjectUtil {

	
	public static String retrieveJson(String url) {
		InputStream is = null;
		String jsonString = "";
		try {
			is = new URL(url).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				jsonString = readAll(rd);
				//System.out.println(jsonString);
			} finally {
				is.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		return jsonString;
	}
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	public static String retrieveJsonPath (JSONObject json, String path){
		
		if (!path.contains("/"))  {
			if (json.get(path) !=null) {
				return json.get(path).toString();
			} else {
				System.out.println("No Path found");
				return "";
			}
			
		} else {
			String[] paths = path.split("/");
			JSONObject newJson = (JSONObject) json.get(paths[0]);
			String newPath = path.substring(path.indexOf("/") + 1);
			return retrieveJsonPath(newJson, newPath);
		}
	}
	
}
