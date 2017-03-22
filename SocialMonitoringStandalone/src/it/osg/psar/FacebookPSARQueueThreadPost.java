package it.osg.psar;

import facebook4j.FacebookException;
import facebook4j.Post;
import it.osg.data.PSAR;
import it.osg.runnable.RunnableQueueBaseInfoImpl;
import it.osg.runnable.RunnableQueueImpl;
import it.osg.exception.ArgumentException;
import it.osg.utils.Configuration;
import it.osg.utils.DateUtils;
import it.osg.utils.FacebookUtils;
import it.osg.utils.SocialLogger;
import it.queue.Queue;
import it.queue.TimeoutException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


public class FacebookPSARQueueThreadPost {

	public static String resourcesFolder = "resources/";
	public static char inputCharDelimiter = ';';

	public static String idField = "pageID";
	public static String nomeField = "nome";
	
//	public static long QUEUE_CHECK_SLEEP = 5000L; //tempo dopo il quale viene eseguito il check per capire: 1)se il timeout è stato superato; 2)se può far partire nuovi thread prelevandoli dalla coda

	private static Logger LOGGER = Logger.getLogger(FacebookPSARQueueThreadPost.class.getName());
	
	public static void main(String[] args) {
		System.getProperties().put("http.proxyHost", "proxy.gss.rete.poste");
		System.getProperties().put("http.proxyPort", "8080");
		System.getProperties().put("http.proxyUser", "rete\\servill7");
		System.getProperties().put("http.proxyPassword", "Paolos51");
		SocialLogger.setup(Level.INFO);
		try {
			compute("quotidiani.csv", "01-01-2014 00:00:00", "01-02-2014 00:00:00", 30, 1000000L, "output/");
		} catch (ArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public static void compute(String inputFile, String from, String to, int lenght, long timeout, String outFold) throws ArgumentException {

		Queue queuePSAR = new Queue(lenght, Configuration.QUEUE_CHECK_SLEEP, timeout);
		queuePSAR.setName("PSAR");
		queuePSAR.setRollback(true);
		Queue queueBaseInfo = new Queue(lenght, Configuration.QUEUE_CHECK_SLEEP, timeout);
		queueBaseInfo.setName("BASE INFO");
		queueBaseInfo.setRollback(false);
		
		long currentTime = System.currentTimeMillis();
		Date currentDate = new Date(currentTime);
		SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
		String time = sdf.format(currentDate);
		String input = inputFile.split("\\.")[0];
//		String psarFileName = outFold + "FB_" + from + "_TO_" + to + "_PSAR_" + input + "-" + time + ".csv";
		String psarFileName = outFold + "FB__TO__PSAR_" + input + "-" + time + ".csv";
		CsvWriter outWriter = openOutputFile(psarFileName);
		LOGGER.info("Output File name: " + psarFileName);
		
		Hashtable<String, String> ids = getInputAccounts(resourcesFolder + inputFile, idField, nomeField, inputCharDelimiter);
		Hashtable<String, PSAR> result = new Hashtable<String, PSAR>();

		//GET TO DATE
		String fromDay = from + " 00:00:00";
		String toDay = to + " 23:59:59";
		Date f = null;
		Date t = null;
		try {
			f = DateUtils.parseDateAndTime(fromDay);
			t = DateUtils.parseDateAndTime(toDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Enumeration<String> keys = ids.keys();
		LOGGER.info("Facebook Search API started");
		while (keys.hasMoreElements()) {
			String currID = keys.nextElement();
			PSAR idPSAR = getIdPSAR(result, currID, ids.get(currID));
			LOGGER.info("Searching for " + ids.get(currID) + " posts");
			
			//Get all Post
			ArrayList<Post> posts = new ArrayList<Post>();
			try {
				posts =	FacebookUtils.getAllPosts(currID, f, t, new String[]{"id"});
			} catch (FacebookException e) {
				e.printStackTrace();
			}

			//SPLITTO 1 POST ALLA VOLTA ED INSERISCO UNA QUEUE DI THREAD
			LOGGER.info("Creating workers for " + ids.get(currID));
			Iterator<Post> iter = posts.iterator();
			while (iter.hasNext()) {
				Post currPost = iter.next();
				RunnableQueueImpl worker = new RunnableQueueImpl(currID, currPost.getId(), idPSAR);
				worker.setName(currPost.getId());
				queuePSAR.addThread(worker);
			}	

		}

		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		try {
			start = System.currentTimeMillis();
			LOGGER.info("Starting PSAR Queue");
			queuePSAR.executeAndWait();
			end = System.currentTimeMillis();
			LOGGER.info("Elapsed Time for Queue: " + queuePSAR.getName() + ": " + (end - start)/1000 + " seconds");
		} catch (TimeoutException e1) {
			e1.printStackTrace();
			LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
		}
		
		
		/*Coda per le BaseInfo*/
		LOGGER.info("Creating Base Info Queue");	
		Enumeration<PSAR> psars = result.elements();
		while (psars.hasMoreElements()) {
			PSAR curr = psars.nextElement();
			/*Aggiungo un worker alla coda delle BaseInfo*/
			RunnableQueueBaseInfoImpl workerBaseInfo = new RunnableQueueBaseInfoImpl(curr);
			workerBaseInfo.setName(curr.getId());
			queueBaseInfo.addThread(workerBaseInfo);
		}
		
		/*Avvio la coda*/
		try {
			LOGGER.info("Starting Base Info Queue");
			start = System.currentTimeMillis();
			queueBaseInfo.executeAndWait();
			end = System.currentTimeMillis();
			LOGGER.info("Elapsed Time for Base Info Queue: " + (end - start)/1000 + " seconds");
		} catch (TimeoutException e1) {
			e1.printStackTrace();
			LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
		}
		
		LOGGER.info("Writing result to file");
		Enumeration<PSAR> elements = result.elements();
		while (elements.hasMoreElements()) {
			PSAR curr = elements.nextElement();
			try {
				outWriter.write(curr.getNome());
				outWriter.write(curr.getId());
				outWriter.write(curr.getPostFromPage().toString());
				outWriter.write(curr.getPostFromFan().toString());
				outWriter.write(curr.getComments().toString());
				outWriter.write(curr.getLikes().toString());
				outWriter.write(curr.getShares().toString());
				outWriter.write(curr.getCommnetsFromPageToPostFromPage().toString());				
				outWriter.write(curr.getCommnetsFromPageToPostFromFan().toString());
				outWriter.write(curr.getFan());
				outWriter.endRecord();
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

		}
		outWriter.close();
		LOGGER.info("STABBENE!!!");

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
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outFile, true), inputCharDelimiter);

			// if the file didn't already exist then we need to write out the header line
			if (!alreadyExists)	{
				csvOutput.write("nome");
				csvOutput.write("id");
				csvOutput.write("post");
				csvOutput.write("fanPost");
				csvOutput.write("comments");
				csvOutput.write("likes");
				csvOutput.write("shares");
				csvOutput.write("commentsToOwnPost");
				csvOutput.write("commentsToOtherPost");
				csvOutput.write("fan");

				csvOutput.endRecord();
			}
			// else assume that the file already has the correct header line
			return csvOutput;
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
			}

			idFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}


}
