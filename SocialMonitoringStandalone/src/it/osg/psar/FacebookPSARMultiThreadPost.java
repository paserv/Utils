package it.osg.psar;

import facebook4j.FacebookException;
import facebook4j.Post;
import it.osg.data.PSAR;
import it.osg.runnable.AtomicPostJob;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


public class FacebookPSARMultiThreadPost {

	public static String inputFile = "resources/facebookIDS.csv";
	public static char inputCharDelimiter = ';';

	public static String outputPath = "resources/facebookModa";
	public static String from = "01-01-2014 00:00:00";
	public static String to = "15-01-2014 23:59:59";

	public static String idField = "pageID";
	public static String nomeField = "nome";

	public static int NTHREADS = 10;
	
	public static void main(String[] args) throws FacebookException {

		System.getProperties().put("http.proxyHost", "proxy.gss.rete.poste");
		System.getProperties().put("http.proxyPort", "8080");
		System.getProperties().put("http.proxyUser", "rete\\servill7");
		System.getProperties().put("http.proxyPassword", "Paolos10");
		
		long start = System.currentTimeMillis();
		CsvWriter outWriter = openOutputFile(outputPath + "_out_" + System.currentTimeMillis() + ".csv");
//		ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(args[0]));
		ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
		
		Hashtable<String, String> ids = getInputAccounts(inputFile, idField, nomeField, inputCharDelimiter);
		Hashtable<String, PSAR> result = new Hashtable<String, PSAR>();

		//GET TO DATE
		String toDay = to.substring(0, 10) + " 23:59:59";
		Date f = null;
		Date t = null;
		try {
			f = DateUtils.parseDateAndTime(from);
			t = DateUtils.parseDateAndTime(toDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Enumeration<String> keys = ids.keys();
		while (keys.hasMoreElements()) {
			String currID = keys.nextElement();
			System.out.println("nome: " + ids.get(currID));
			PSAR idPSAR = getIdPSAR(result, currID, ids.get(currID));

			//Get all Post
			ArrayList<Post> posts = new ArrayList<Post>();
			posts =	FacebookUtils.getAllPosts(currID, f, t, new String[]{"id"});

			//SPLITTO 1 POST ALLA VOLTA ED INSERISCO UN THREAD NEL POOL
			Iterator<Post> iter = posts.iterator();
			while (iter.hasNext()) {
				Post currPost = iter.next();
				Runnable worker = new AtomicPostJob(currID, currPost.getId(), idPSAR, executor);
				executor.execute(worker);
			}	

		}

		executor.shutdown();
		
		try {
			if (!executor.awaitTermination(60000, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Enumeration<PSAR> elements = result.elements();
		while (elements.hasMoreElements()) {
			PSAR curr = elements.nextElement();
			try {
				outWriter.write(curr.getId());
				outWriter.write(curr.getNome());
				outWriter.write(curr.getPostFromPage().toString());
				outWriter.write(curr.getPostFromFan().toString());
				outWriter.write(curr.getComments().toString());
				outWriter.write(curr.getLikes().toString());
				outWriter.write(curr.getShares().toString());
				outWriter.write(curr.getCommentsToPostFromFan().toString());
				outWriter.write(curr.getCommnetsFromPageToPostFromPage().toString());
				outWriter.write(curr.getCommnetsFromPageToPostFromFan().toString());
				outWriter.endRecord();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		outWriter.close();

		long end = System.currentTimeMillis();
		System.out.println("Elapsed Time: " + (end - start)/1000 + " seconds");

	}


	private static PSAR getIdPSAR(Hashtable<String, PSAR> result, String currID, String string) {
		if (result.get(currID) != null) {
			return result.get(currID);
		}
		PSAR newPSAR = new PSAR(currID, string);
		result.put(currID, newPSAR);
		return newPSAR;
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
