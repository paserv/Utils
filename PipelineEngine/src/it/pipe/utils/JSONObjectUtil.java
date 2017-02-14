package it.pipe.utils;

import org.json.simple.JSONObject;

public class JSONObjectUtil {

	public static String retrieveJsonPath (JSONObject json, String path){
		
		if (!path.contains("/"))  {
			if (json.get(path) != null) {
				return json.get(path).toString();
			} else {
				return null;
			}
			
		} else {
			String[] paths = path.split("/");
			JSONObject newJson = (JSONObject) json.get(paths[0]);
			String newPath = path.substring(path.indexOf("/") + 1);
			return retrieveJsonPath(newJson, newPath);
		}
	}
	
}
