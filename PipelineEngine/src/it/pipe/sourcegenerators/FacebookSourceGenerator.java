package it.pipe.sourcegenerators;

import it.pipe.core.SourceGenerator;
import it.pipe.utils.JSONObjectUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class FacebookSourceGenerator extends SourceGenerator {

	public FacebookSourceGenerator() {
		super();
	}
	
	public FacebookSourceGenerator(String modName, String conFilePath) {
		super(modName, conFilePath);
		//System.setProperty("java.net.useSystemProxies", "true");
	}

	@Override
	public ArrayList<String> add() {
		ArrayList<String> result = new ArrayList<String>();

		InputStream is = null;
		try {
			String url = config.get("url") + "/" + config.get("page") + "/" + config.get("query") + "?access_token=" + config.get("access_token") + "&since=" + convertDataToUnixTime(config.get("since")) + "&until=" + convertDataToUnixTime(config.get("until")) + "&limit=" + config.get("limit");
			//System.out.println(url);
			is = new URL(url).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonString = readAll(rd);
				//System.out.println(jsonString);
				Object objJson = JSONValue.parse(jsonString);
				JSONObject json =(JSONObject) objJson;
				JSONArray dataArray = (JSONArray) json.get("data");
				if (dataArray != null){
					for (int i = 0; i < dataArray.size(); i++) {
						JSONObject currJsonData =(JSONObject) dataArray.get(i);
						String message = JSONObjectUtil.retrieveJsonPath(currJsonData, "message");
						String story = JSONObjectUtil.retrieveJsonPath(currJsonData, "story");
						if (message != null) {
							result.add(message);
						}
						if (story != null) {
							result.add(story);
						}
						JSONObject commentsObj = (JSONObject) currJsonData.get("comments");
						JSONArray commentsArray = (JSONArray) commentsObj.get("data");
						for (int j = 0; j < commentsArray.size(); j++) {
							JSONObject currJsonCom =(JSONObject) commentsArray.get(j);
							String commentMessage = JSONObjectUtil.retrieveJsonPath(currJsonCom, "message");
							//System.out.println(commentMessage);
							result.add(commentMessage);
							//TODO gestire la pagination
						}



					}
				}
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



		return result;
	}



	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private static String convertDataToUnixTime(String data) {
		String unixTime = null;
		DateFormat dfm = new SimpleDateFormat("dd/MM/yyyy-HH:mm");  
		try {
			long unixTimeL = dfm.parse(data).getTime();
			unixTime = String.valueOf(unixTimeL);
			unixTime = unixTime.substring(0, unixTime.length()-3);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return unixTime;
	}

	public static void main(String[] args) {



		System.out.println("=======Esempio Json Parser=======");

		String s="{\"glossary\": {" +
				"\"title\": \"example glossary\"," +
				"\"GlossDiv\": {" +
				"\"title\": \"S\"," +
				"\"GlossList\": {" +
				"\"GlossEntry\": {" +
				"\"ID\": \"SGML\"," +
				"\"SortAs\": \"SGML\"," +
				"\"GlossTerm\": \"Standard Generalized Markup Language\"," +
				"\"Acronym\": \"SGML\"," +
				"\"Abbrev\": \"ISO 8879:1986\"," +
				"\"GlossDef\": {" +
				"\"para\": \"A meta-markup language, used to create markup languages such as DocBook.\"," +
				"\"GlossSeeAlso\": [\"GML\", \"XML\"]" +
				"}," +
				"\"GlossSee\": \"markup\"" +
				"}" +
				"}" +
				"}" +
				"}" +
				"}";


		Object obj = JSONValue.parse(s);
		JSONObject json =(JSONObject) obj;
		System.out.println(JSONObjectUtil.retrieveJsonPath(json, "glossary/GlossDiv/GlossList/GlossEntry/SortAs"));

		//		System.setProperty("proxySet", "true");
		//		System.setProperty("http.proxyHost", "proxy host addr");
		//		System.setProperty("http.proxyPort", "808");
		//		Authenticator.setDefault(new Authenticator() {
		//		    protected PasswordAuthentication getPasswordAuthentication() {
		//
		//		        return new PasswordAuthentication("domain\\user","password".toCharArray());
		//		    }
		//		});
		//		
		System.setProperty("java.net.useSystemProxies", "true");

		URL oracle;
		try {
			oracle = new URL("http://www.oracle.com/");
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

}
