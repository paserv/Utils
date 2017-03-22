package it.osg.psar;

import it.osg.data.PSAR;
import it.osg.runnable.AtomicJob;
import it.osg.utils.DateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


public class FacebookPSARMultiThreadData {

	public static String inputFile = "resources/facebookIDS.csv";
	public static char inputCharDelimiter = ';';

	public static String outputPath = "resources/facebookModa";
	public static String from = "01-01-2014 00:00:00";
	public static String to = "02-01-2014 23:59:59";

	public static String idField = "pageID";
	public static String nomeField = "nome";

	public static int NTHREADS = 100;
	public static int STEP = 4;

	public static void main(String[] args) {

		System.getProperties().put("http.proxyHost", "proxy.gss.rete.poste");
		System.getProperties().put("http.proxyPort", "8080");
		System.getProperties().put("http.proxyUser", "rete\\servill7");
		System.getProperties().put("http.proxyPassword", "Paolos10");
		
		long start = System.currentTimeMillis();
		
		CsvWriter outWriter = openOutputFile(outputPath + "_out_" + System.currentTimeMillis() + ".csv");
		ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);


		Hashtable<String, String> ids = getInputAccounts(inputFile, idField, nomeField, inputCharDelimiter);

		//GET TO DATE
		String toDay = to.substring(0, 10) + " 23:59:59";
		Date t = null;
		try {
			t = DateUtils.parseDateAndTime(toDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}


		Enumeration<String> keys = ids.keys();
		Hashtable<String, PSAR> resultPSAR = new Hashtable<String, PSAR>();

		while (keys.hasMoreElements()) {
			String currID = keys.nextElement();
			System.out.println("nome: " + ids.get(currID));
			PSAR currPSAR = new PSAR(currID, ids.get(currID));

			//SPLITTO N GIORNI ALLA VOLTA ED INSERISCO UN THREAD NEL POOL
			String from1 = from.substring(0, 10) + " 00:00:00";
			try {
				Date f1 = DateUtils.parseDateAndTime(from1);

				while (true) {
					String to1 = DateUtils.formatDateAndTime(DateUtils.addHoursToDate(DateUtils.parseDateAndTime(from1), STEP));
					Date t1 = DateUtils.parseDateAndTime(to1);
					if (DateUtils.compareDate(t, t1) >= 0) {
						Runnable worker = new AtomicJob(currID, from1, to1, currPSAR);
						executor.execute(worker);
						f1 = t1;
						from1 = DateUtils.formatDateAndTime(f1);
					} else {
						Runnable worker = new AtomicJob(currID, from1, toDay, currPSAR);
						executor.execute(worker);
						break;
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

			} catch (ParseException e) {
				e.printStackTrace();
			}

			resultPSAR.put(currID, currPSAR);

		}


		Enumeration<PSAR> elements = resultPSAR.elements();
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
