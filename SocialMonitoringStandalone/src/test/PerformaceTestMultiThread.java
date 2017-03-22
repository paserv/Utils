package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import com.csvreader.CsvWriter;

import it.osg.psar.FacebookPSARQueueThreadPost;
import it.osg.exception.ArgumentException;

public class PerformaceTestMultiThread {

	public static String inputFile = "quotidiani.csv";
	public static String from = "01-01-2014 00:00:00";
	public static String to = "15-01-2014 23:59:59";

	public static String QUEUE_CHECK_SLEEP = "5000"; //tempo dopo il quale viene eseguito il check per capire: 1)se il timeout è stato superato; 2)se può far partire nuovi thread prelevandoli dalla coda
	public static long QUEUE_TIMEOUT = 1000000000L; //timeout della coda
	
	public static void main(String[] args) {

		//		System.getProperties().put("http.proxyHost", "proxy.gss.rete.poste");
		//		System.getProperties().put("http.proxyPort", "8080");
		//		System.getProperties().put("http.proxyUser", "rete\\servill7");
		//		System.getProperties().put("http.proxyPassword", "Paolos10");

		//		QueueLogger.setup(Level.OFF);

		File result = new File("resources\\result.csv");
		try {
			OutputStream stream = new FileOutputStream(result);
			stream.write("pablo".getBytes());
			stream.write("\\n".getBytes());
			
			int mult = 1;
			for (int i = 1; i <= 8; i++) {
				mult = 2 * mult;
				String numThreadsString = String.valueOf(mult);
				int lenght = Integer.valueOf(numThreadsString);
				long start = System.currentTimeMillis();
				try {
					FacebookPSARQueueThreadPost.compute(inputFile, from, to, lenght, QUEUE_TIMEOUT, "output");
				} catch (ArgumentException e) {
					e.printStackTrace();
				}
				
				long end = System.currentTimeMillis();
				long elapsedTime = (end - start)/1000;
				System.out.println("ELAPSED: " + elapsedTime);
				stream.write(numThreadsString.getBytes());
				stream.write(";".getBytes());
				stream.write(String.valueOf(elapsedTime).getBytes());
				stream.write("acapo".getBytes());
			}
			stream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private static CsvWriter openOutputFile(String outFile) {

		boolean alreadyExists = new File(outFile).exists();

		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outFile, true), ',');

			// if the file didn't already exist then we need to write out the header line
			if (!alreadyExists)	{
				csvOutput.write("NumThread");
				csvOutput.write("Time");
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
