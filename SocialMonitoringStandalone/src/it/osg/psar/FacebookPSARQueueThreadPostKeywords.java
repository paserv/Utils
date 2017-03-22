package it.osg.psar;

import facebook4j.FacebookException;
import facebook4j.Post;
import it.osg.data.PSAR;
import it.osg.data.Words;
import it.osg.runnable.RunnableQueueBaseInfoImpl;
import it.osg.runnable.RunnableQueueImpl;
import it.osg.runnable.RunnableQueueImpl4Words;
import it.osg.utils.FacebookUtils;
import it.pipe.core.PipeBlock;
import it.pipe.core.PipelineEngine;
import it.pipe.filters.RemoveRegex;
import it.pipe.filters.RemoveWordList;
import it.pipe.transformers.Tokenizer;
import it.pipe.writers.FrequencyWriter;
import it.queue.Queue;
import it.queue.TimeoutException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Logger;

import com.csvreader.CsvWriter;


public class FacebookPSARQueueThreadPostKeywords {

	public static String resourcesFolder = "resources/";
	public static String outputFolder = "output/";
	public static char inputCharDelimiter = ';';

	public static String inputFile = "quotidiani.csv";
	public static String from = "13-05-2014 00:00:00";
	public static String to = "13-05-2014 23:59:59";

	public static int QUEUELENGHT = 20; //Numero massimo thread in stato running
	public static long QUEUE_CHECK_SLEEP = 5000L; //tempo dopo il quale viene eseguito il check per capire: 1)se il timeout è stato superato; 2)se può far partire nuovi thread prelevandoli dalla coda
	public static long QUEUE_TIMEOUT = 1000000000L; //timeout della coda

	public static String keywordString = "renzi";
	public static String keywordOperator = "OR";
	
	private static Logger LOGGER = Logger.getLogger(FacebookPSARQueueThreadPostKeywords.class.getName());

	public static void main(String[] args) throws FacebookException {

//		System.getProperties().put("http.proxyHost", "proxy.gss.rete.poste");
//		System.getProperties().put("http.proxyPort", "8080");
//		System.getProperties().put("http.proxyUser", "rete\\servill7");
//		System.getProperties().put("http.proxyPassword", "Paolos10");

		if (args != null && args.length > 0) {
			from = args[0];
			to = args[1];
			QUEUELENGHT = Integer.valueOf(args[2]);
			QUEUE_CHECK_SLEEP = Long.valueOf(args[3]);
			QUEUE_TIMEOUT = Long.valueOf(args[4]);
			keywordString = args[5];
			keywordOperator = args[6];
			outputFolder= args[7];
            resourcesFolder= args[7];
		}

		//GET DATE
		String fromDay = from.substring(0, 10) + " 00:00:00";
		String toDay = to.substring(0, 10) + " 23:59:59";

		/*Instanzio le code*/
		Queue queuePSAR = new Queue(QUEUELENGHT, QUEUE_CHECK_SLEEP, QUEUE_TIMEOUT);
		queuePSAR.setRollback(false);
		queuePSAR.setName("PSAR Queue");
		Queue queueHashtags = new Queue(QUEUELENGHT, QUEUE_CHECK_SLEEP, QUEUE_TIMEOUT);
		queueHashtags.setRollback(false);
		queuePSAR.setName("Word Queue");
		Queue queueBaseInfo = new Queue(QUEUELENGHT, QUEUE_CHECK_SLEEP, QUEUE_TIMEOUT);
		queueBaseInfo.setRollback(false);

		/*Instanzio gli oggetti funzionali alla scrittura dei file di output*/
		Hashtable<String, PSAR> resultPSAR = new Hashtable<String, PSAR>();
//		Hashtags resultHashtags = new Hashtags();

		/*Instanzio il writer dei file di output per il calcolo PSAR*/
		String psarFileName = "FB_" + from.substring(0, 10) + "_TO_" + to.substring(0,10) + "_KEY_" + keywordString + "_" + keywordOperator + "_" + System.currentTimeMillis() + ".csv";
		CsvWriter outWriterPSAR = openOutputFile(outputFolder + psarFileName);
		/*Imposto il nome per il file degli hashtag*/ 
		String hashTagFileName = psarFileName.substring(0, psarFileName.length() - 4) + ".hashtag";
		LOGGER.info("Output File name: " + psarFileName);
		
		/*Gestione dell' AND o OR delle keyword*/
		String[] keywords = keywordString.split(",");
		if (keywordOperator.equalsIgnoreCase("AND")) {
			keywords = new String[1];
			keywords[0] = keywordString;
		}

		/** Lancio le ricerca sulle keywords, se per esempio
		 * in un post sono presenti 2 keywords,
		 * allora nell'ArrayList sarà presente 2 volte lo stesso autore
		 * nelle cui hashtable di post sarà presente 2 volte lo stesso post */
		LOGGER.info("Facebook Search API started");
		ArrayList<Hashtable<String, Hashtable<String, Post>>> postsGroupedByAuthor = new ArrayList<Hashtable<String,Hashtable<String, Post>>>();
		for (int i = 0; i < keywords.length; i++) {
			String currKeyword = keywords[i];
			LOGGER.info("\t\tKeyword: " + currKeyword);
			Hashtable<String, Hashtable<String, Post>> currKeywordPosts = FacebookUtils.getPostByKeyword(currKeyword, fromDay, toDay);
			postsGroupedByAuthor.add(currKeywordPosts);
		}

		/** Qui ri-raggruppo per autore in modo da deduplicare i post */
		LOGGER.info("Remove duplicate Post");
		Hashtable<String, Hashtable<String, Post>> authorsWithPost = new Hashtable<String, Hashtable<String, Post>>();
		Iterator<Hashtable<String, Hashtable<String, Post>>> iter = postsGroupedByAuthor.iterator();
		while (iter.hasNext()) {
			Hashtable<String, Hashtable<String, Post>> current = iter.next();
			Enumeration<String> keys = current.keys();
			while (keys.hasMoreElements()) {
				String currKey = keys.nextElement();
				if (authorsWithPost.containsKey(currKey)) {	
					Hashtable<String, Post> alreadyExistentPostSet = authorsWithPost.get(currKey);
					alreadyExistentPostSet.putAll(current.get(currKey));					
				} else {
					authorsWithPost.putAll(current);
				}

			}

		}


		/** Per ogni autore e per ogni suo post metto un thread in coda per il calcolo di PSAR
		 *  e un thread in coda per il calcolo delle frequenze delle words */
		LOGGER.info("Creating Working Queues: PSAR Queue - Word Queue");
		Words arrayWord = new Words();
		Enumeration<String> authors = authorsWithPost.keys();
		while (authors.hasMoreElements()) {
			String currAuthorID = authors.nextElement();
			PSAR idPSAR = getIdPSAR(resultPSAR, currAuthorID, currAuthorID);

			Hashtable<String, Post> posts = authorsWithPost.get(currAuthorID);

			//SPLITTO 1 POST ALLA VOLTA ED INSERISCO UNA QUEUE DI THREAD
			Enumeration<Post> enumPosts = posts.elements();
			while (enumPosts.hasMoreElements()) {
				Post currPost = enumPosts.nextElement();

				/*Aggiungo un worker alla coda degli hashtags*/
//				RunnableQueueImpl4Hashtags workerHashtags = new RunnableQueueImpl4Hashtags(currPost, resultHashtags);
//				workerHashtags.setName(currPost.getId() + "_ht");
//				queueHashtags.addThread(workerHashtags);
				/*Aggiungo un worker alla coda delle words*/
				RunnableQueueImpl4Words workerHashtags = new RunnableQueueImpl4Words(currPost, arrayWord);
				workerHashtags.setName(currPost.getId() + "_ht");
				queueHashtags.addThread(workerHashtags);

				/*Aggiungo un worker alla coda dei PSAR*/
				RunnableQueueImpl workerPSAR = new RunnableQueueImpl(currAuthorID, currPost.getId(), idPSAR);
				workerPSAR.setName(currPost.getId());
				queuePSAR.addThread(workerPSAR);
			}
		}


		/*Avvio le code*/
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		try {
			LOGGER.info("Starting PSAR Queue");
			start = System.currentTimeMillis();
			queuePSAR.executeAndWait();
			end = System.currentTimeMillis();
			LOGGER.info("Elapsed Time for PSAR Queue: " + (end - start)/1000 + " seconds");
			
			LOGGER.info("Starting Word Queue");
			start = System.currentTimeMillis();
			queueHashtags.executeAndWait();
			end = System.currentTimeMillis();
			LOGGER.info("Elapsed Time for Word Queue: " + (end - start)/1000 + " seconds");
			
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		}

		/*Coda per le BaseInfo*/
		LOGGER.info("Creating Base Info Queue");	
		Enumeration<PSAR> psars = resultPSAR.elements();
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
		}		
		
		LOGGER.info("Creating PSAR file");
		/*File per i risultati di PSAR*/
		Enumeration<PSAR> elements = resultPSAR.elements();
		while (elements.hasMoreElements()) {
			PSAR curr = elements.nextElement();
			if (curr.getPostFromPage().intValue() != 0) {
				if (curr.getFan() != null && !curr.getFan().equals("")) {
					try {
						if (curr.getNome() != null && !curr.getNome().equals("")) {
							outWriterPSAR.write(curr.getNome());
						} else {
							outWriterPSAR.write("No Name Found");
						}
						outWriterPSAR.write(curr.getId());
						outWriterPSAR.write(curr.getPostFromPage().toString());
						outWriterPSAR.write(curr.getPostFromFan().toString());
						outWriterPSAR.write(curr.getComments().toString());
						outWriterPSAR.write(curr.getLikes().toString());
						outWriterPSAR.write(curr.getShares().toString());
						outWriterPSAR.write(curr.getCommnetsFromPageToPostFromPage().toString());				
						outWriterPSAR.write(curr.getCommnetsFromPageToPostFromFan().toString());
						outWriterPSAR.write(curr.getFan());
						outWriterPSAR.endRecord();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}


		}
		outWriterPSAR.close();


		/*File per i risultati Hashtags*/
//		List<Entry<String>> sortedHt = sortMultisetPerEntryCount(resultHashtags.getResultHashtags());
//		int topHTNumber;
//		if (sortedHt.size() < 100) {
//			topHTNumber = sortedHt.size();
//		} else {
//			topHTNumber = 100;
//		}
//
//		List<Entry<String>> topHunHT = sortedHt.subList(0, topHTNumber);
//		for (int i = 0; i < topHunHT.size(); i++) {
//			try {
//				outWriterHashtags.write(topHunHT.get(i).getElement());
//				outWriterHashtags.write(String.valueOf(topHunHT.get(i).getCount()));
//				outWriterHashtags.endRecord();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//		outWriterHashtags.close();
		
		LOGGER.info("Creating Word Cloud file");
		/*Cleansing e File per i risultati Hashtags*/
		PipelineEngine eng = new PipelineEngine();
		/*Imposto come input l'array di words calcolato*/
		eng.setInput(arrayWord.getWords());
		/*Modulo di tokenizzazione*/
		PipeBlock firstBlock = new Tokenizer();
		/*Modulo per rimuovere stopwords*/
		PipeBlock secondBlock = new RemoveWordList();
		secondBlock.setProperty("vocabularyPath1", resourcesFolder + "gazetteers/stopwords_it.gaz");
		/*Modulo per rimuovere regular expressions*/		
		PipeBlock thirdBlock = new RemoveRegex();
		thirdBlock.setProperty("regex1", "^[0-9]+");
		/*Modulo per la scrittura del file con le frequenze delle words*/
		PipeBlock fourthBlock = new FrequencyWriter();
		fourthBlock.setProperty("path", outputFolder + hashTagFileName);
		fourthBlock.setProperty("max", "100");
		fourthBlock.setProperty("separator", ";");
		/*aggiungo i moduli all'engine*/
		eng.addBlock(firstBlock);
		eng.addBlock(secondBlock);
		eng.addBlock(thirdBlock);
		eng.addBlock(fourthBlock);
		/*faccio partire l'engine*/
		eng.run();
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
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outFile, true), ';');

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
		}

		return null;
	}



}
