package it.osg.psar;

import facebook4j.Comment;
import facebook4j.FacebookException;
import facebook4j.Like;
import facebook4j.Post;
import it.osg.utils.DateUtils;
import it.osg.utils.FacebookUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


public class FacebookPSAR {

	public static String inputFile = "resources/facebookIDS.csv";
	public static char inputCharDelimiter = ';';
	
	public static String outputPath = "resources/facebookModa";
	public static String from = "01-01-2014 00:00:00";
	public static String to = "15-01-2014 23:59:59";
	
	public static String idField = "pageID";
	public static String nomeField = "nome";

	public static void main(String[] args) throws FacebookException {

		long start = System.currentTimeMillis();
		
		CsvWriter outWriter = openOutputFile(outputPath + "_out_" + System.currentTimeMillis() + ".csv");

		Hashtable<String, String> ids = getInputAccounts(inputFile, idField, nomeField, inputCharDelimiter);

		//INPUT PARAMETER
		Date f = null;
		Date t = null;
		try {

			f = DateUtils.parseDateAndTime(from);
			t = DateUtils.parseDateAndTime(to);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Enumeration<String> keys = ids.keys();
		while (keys.hasMoreElements()) {
			String currID = keys.nextElement();
			System.out.println("nome: " + ids.get(currID));

			//OUTPUT DATA
			double postFromPage = 0;
			double postFromFan = 0;
			double comments = 0;
			//String authors = "";
			double likes = 0;
			double shares = 0;

			double commentsToPostFromFan = 0;
			double commnetsFromPageToPostFromPage = 0;
			//double commnetsFromPageToPostFromPageNoDuplicate = 0;
			double commnetsFromPageToPostFromFan = 0;
			//double commnetsFromPageToPostFromFanNoDuplicate = 0;

			//Get all Post
			ArrayList<Post> posts = new ArrayList<Post>();
			posts =	FacebookUtils.getAllPosts(currID, f, t, null);

			ArrayList<Post> postFromPageList = new ArrayList<Post>();
			ArrayList<Post> postFromFanList = new ArrayList<Post>();

			Iterator<Post> iterPost = posts.iterator();
			while (iterPost.hasNext()) {
				Post currPost = iterPost.next();
				if (currPost.getFrom().getId().equals(currID) && currPost.getMessage() != null) {
					postFromPageList.add(currPost);
				} else {
					postFromFanList.add(currPost);
				}
			}


			//P^{x}[i,j] + S^{x}[i,j] Link, Status, Photo, Video from page 
			postFromPage = postFromPageList.size();
			postFromFan = postFromFanList.size();
			System.out.println("postFromPage: " + postFromPage);
			System.out.println("postFromFan: " + postFromFan);

			//Comments to Post from page
			ArrayList<Comment> commentsList = FacebookUtils.getComments(postFromPageList);
			//C^{x}_{Post^{x}}[i,j] 
			comments = commentsList.size();
			System.out.println("comments: " + comments);

			//Get all Comments from FAN
			ArrayList<Comment> commentsToPostFromFanList = FacebookUtils.getComments(postFromFanList);
			commentsToPostFromFan = commentsToPostFromFanList.size();
			System.out.println("commentsToPostFromFan: " + commentsToPostFromFan);



			//Get all Unique Authors of Comments
			//		ArrayList<String> uniqueAuth = FacebookUtils.getUniqueAuthors(comments);
			//		Iterator<String> iter = uniqueAuth.iterator();
			//		while (iter.hasNext()) {
			//			authors = authors + iter.next() + ",";
			//		}

			//Get all Likes to Posts
			ArrayList<Like> likesList = FacebookUtils.getLikes(postFromPageList);
			//L_{P^{x}}[i,j] 
			likes = likesList.size();
			System.out.println("likes: " + likes);

			//S_{P^{x}}[i,j] Get all Shares to Posts
			shares = FacebookUtils.getShares(postFromPageList);
			System.out.println("shares: " + shares);

			//C^{x}_{Post^{x}}[i,j]		
			commnetsFromPageToPostFromPage = FacebookUtils.getCommentsFromIdCount(currID, commentsList);
			//C_{Post^{z\neq x}}^{x}[i,j]
			commnetsFromPageToPostFromFan = FacebookUtils.getCommentsFromIdCount(currID, commentsToPostFromFanList);

			System.out.println("commnetsFromPageToPostFromPage: " + commnetsFromPageToPostFromPage);
			System.out.println("commnetsFromPageToPostFromFan: " + commnetsFromPageToPostFromFan);
			
			//WRITE TO OUTPUT FILE
			try {
				outWriter.write(currID);
				outWriter.write(ids.get(currID));
				outWriter.write(String.valueOf(postFromPage));
				outWriter.write(String.valueOf(postFromFan));
				outWriter.write(String.valueOf(comments));
				outWriter.write(String.valueOf(likes));
				outWriter.write(String.valueOf(shares));
				outWriter.write(String.valueOf(commentsToPostFromFan));
				outWriter.write(String.valueOf(commnetsFromPageToPostFromPage));
				outWriter.write(String.valueOf(commnetsFromPageToPostFromFan));
				
				outWriter.endRecord();
//				currEntity.setProperty("authors", new Text(authors));
				
				System.out.println("ID: " + currID);
				System.out.println("nome: " + ids.get(currID));
				System.out.println("postFromPage: " + postFromPage);
				System.out.println("postFromFan: " + postFromFan);
				System.out.println("comments: " + comments);
				System.out.println("likes: " + likes);
				System.out.println("shares: " + shares);
				System.out.println("commentsToPostFromFan: " + commentsToPostFromFan);
				System.out.println("commnetsFromPageToPostFromPage: " + commnetsFromPageToPostFromPage);
				System.out.println("commnetsFromPageToPostFromFan: " + commnetsFromPageToPostFromFan);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			

			
		}

		outWriter.close();
		
		long end = System.currentTimeMillis();
		System.out.println("Elapsed Time: " + (end - start)/1000 + " seconds");

	}


	private static CsvWriter openOutputFile(String outFile) {

		boolean alreadyExists = new File(outFile).exists();

		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outFile, true), ',');

			// if the file didn't already exist then we need to write out the header line
			if (!alreadyExists)	{
				csvOutput.write("id");
				csvOutput.write("nome");
				csvOutput.write("PostFromPage");
				csvOutput.write("PostFromFan");
				csvOutput.write("Comments");
				csvOutput.write("Likes");
				csvOutput.write("Shares");
				csvOutput.write("CommentsToPostFromFan");
				csvOutput.write("CommnetsFromPageToPostFromPage");
				csvOutput.write("CommnetsFromPageToPostFromFan");
				
				csvOutput.endRecord();
			}
			// else assume that the file already has the correct header line
			return csvOutput;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}


	private static Hashtable<String, String> getInputAccounts(String inFile, String idField2, String nomeField2, char inputCharDelimiter2) {

		Hashtable<String, String> result = new Hashtable<String, String>();

		CsvReader idFile;
		try {
			idFile = new CsvReader(inFile);
			idFile.setDelimiter(inputCharDelimiter2);
			idFile.readHeaders();

			while (idFile.readRecord())	{
				String accountID = idFile.get(idField2);
				String name = idFile.get(nomeField2);
				result.put(accountID, name);
				System.out.println(accountID + ":" + name);
			}

			idFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}


}
