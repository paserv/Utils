package it.osg.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;


public class Utils {

	public static String MD5(String string) throws UnsupportedEncodingException {  
		String signature = null;

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(string.getBytes(),0,string.length());
			signature = new BigInteger(1,md5.digest()).toString(16);
			//System.out.println("Signature: " + signature);
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return signature;
	} 

	
	public static ArrayList<String> getAllSindaci() {
		ArrayList<String> sindaci = new ArrayList<String>();
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q;
		PreparedQuery pq;
		q = new Query("anagraficaSindaco");
		pq = datastore.prepare(q);
		for (Entity ent : pq.asIterable()) {
			String currSindaco = String.valueOf(ent.getKey().getName());
			sindaci.add(currSindaco);
		}
		
		return sindaci;
		
	}
	
	public static String cleanString(String s) {
	    String result = s.replace("\"", "");
	    return result;
	}
	
	public static boolean isDouble(String s) {
	    try { 
	        Double.parseDouble(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}


	public static double trunkateToMax(double d, int i) {
		if (d > i) {
			return i;
		}
		return d;
	}
	
	public static String removeChar(String string, String character) {
		string = string.replace(character, "");
		return string;
		
	}

	public static void main(String[] args) {
		String str = "100,98,912";
		System.out.println(removeChar(str, ","));
	}
	
}
