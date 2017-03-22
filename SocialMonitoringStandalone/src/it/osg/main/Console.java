package it.osg.main;

import it.osg.exception.ArgumentException;
import it.osg.psar.FacebookPSARQueueThreadPost;
import it.osg.utils.Configuration;
import it.osg.utils.SocialLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Console {
	private static final String DATE_FORMAT = "dd-MM-yyyy";
	private static boolean running = false;

	private static String fileName;
	private static String from;
	private static String to;
	private static int QUEUELENGHT;
	private static long QUEUE_TIMEOUT;
//	public static long QUEUE_CHECK_SLEEP = 5000L;
	public static String outputFilePath = "output/";

	private static Logger LOGGER = Logger.getLogger(Console.class.getName());
	
	public static void main(String[] args) {

//				System.getProperties().put("http.proxyHost", "proxy.gss.rete.poste");
//				System.getProperties().put("http.proxyPort", "8080");
//				System.getProperties().put("http.proxyUser", "rete\\servill7");
//				System.getProperties().put("http.proxyPassword", "Paolos11");

		SocialLogger.setup(Level.INFO);
		
		if (args == null || args.length == 0) {
			System.out.println("Actions: run - exit - help");
			System.out.print("> ");
			String action = getParameter();
			if (action.equalsIgnoreCase("run")) {
				running = true;
				System.out.println("Set input file from list");
				ArrayList<String> filesName = readInputFile("resources");
				getFileName(filesName);

				System.out.println("Set FROM date (dd-mm-yyyy)");
				getFromDate();

				System.out.println("Set TO date (dd-mm-yyyy)");
				getToDate();

				System.out.println("Set Queue lenght");
				getQueueLenght();

				System.out.println("Set Queue timeout");
				getQueueTimeout();

//				System.out.println("Set Output path");
//				getOutputPath();

				LOGGER.info("JOB PARAMETERS:\n\tFile name: " + fileName + "\n\tFrom: " + from + "\n\tTo: " + to + "\n\tQueue lenght: " + QUEUELENGHT + "\n\tQueue timeout: " + QUEUE_TIMEOUT);
				LOGGER.info("\nCONFIGURATION PARAMETERS:\n\tROLLBACK_SLEEP: " + Configuration.ROLLBACK_SLEEP + "\n\tQUEUE_CHECK_SLEEP: " + Configuration.QUEUE_CHECK_SLEEP + "\n\tSLIDING_WINDOW: " + Configuration.SLIDING_WINDOW + "\n\tMAX_REQUEST: " + Configuration.MAX_REQUEST + "\n\tTHROTTLING_SLEEP: " + Configuration.THROTTLING_SLEEP + "\n\tOUTPUT_FOLDER: " + Configuration.OUTPUT_FOLDER);
				
				running = false;


				try {
					FacebookPSARQueueThreadPost.compute(fileName, from, to, QUEUELENGHT, QUEUE_TIMEOUT, Configuration.OUTPUT_FOLDER);
				} catch (ArgumentException e) {
					e.printStackTrace();
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}

				main(null);

			} else {
				main(null);
			}
		} else {
			fileName = args[0];
			from = args[1];
			to = args[2];
			QUEUELENGHT = Integer.valueOf(args[3]);
			QUEUE_TIMEOUT = Long.valueOf(args[4]);
			outputFilePath = args[5];
			try {
				FacebookPSARQueueThreadPost.compute(fileName, from, to, QUEUELENGHT, QUEUE_TIMEOUT, outputFilePath);
			} catch (ArgumentException e) {
				e.printStackTrace();
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}



	}

	private static void getOutputPath() {
		System.out.print("> ");
		String param = getParameter();
		File folderFile = new File(param);
		if (folderFile.isDirectory()) {
			outputFilePath = param;
		} else {
			System.out.println("Invalid Path, retry");
			getOutputPath();
		}



	}

	private static void getQueueTimeout() {
		System.out.print("> ");
		String param = getParameter();

		try {
			long timeout = Long.valueOf(param);
			QUEUE_TIMEOUT = timeout;
		} catch (Exception ex) {
			System.out.println("Long not parsable, retry!");
			getQueueTimeout();
			ex.printStackTrace();
		}

	}

	private static void getQueueLenght() {
		System.out.print("> ");
		String param = getParameter();

		try {
			int lenght = Integer.valueOf(param);
			QUEUELENGHT = lenght;
		} catch (Exception ex) {
			System.out.println("Integer not parsable, retry!");
			getQueueLenght();
			ex.printStackTrace();
		}

	}

	private static void getToDate() {
		System.out.print("> ");
		String param = getParameter();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		sdf.setLenient(false);

		try {
			Date toDate = sdf.parse(param);
			Date fromDate = sdf.parse(from);

			Calendar toCal = new GregorianCalendar();
			Calendar fromCal = new GregorianCalendar();
			toCal.setTime(toDate);
			fromCal.setTime(fromDate);

			if (fromCal.before(toCal) || fromCal.equals(toCal)) {
				to = param;
			} else {
				System.out.println("TO date is before FROM date, retry!");
				getToDate();
			}


		} catch (ParseException e) {
			System.out.println("Wrong date, retry!");
			getToDate();
		}

	}

	private static void getFromDate() {
		System.out.print("> ");
		String param = getParameter();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		sdf.setLenient(false);

		try {
			//if not valid, it will throw ParseException
			Date date = sdf.parse(param);
			from = param;

		} catch (ParseException e) {
			System.out.println("Wrong date, retry!");
			getFromDate();
		}

	}

	private static void getFileName(ArrayList<String> list) {
		System.out.print("> ");
		String param = getParameter();
		for (String string : list) {
			if (string.equals(param)) {
				fileName = param;
				return;
			}
		}

		System.out.println("File name not present, retry!");
		getFileName(list);
	}

	private static String getParameter() {
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		try {
			String s = bufferRead.readLine();
			if (s.equalsIgnoreCase("exit")) {
				System.exit(0);
			} else if (s.equalsIgnoreCase("help") && !running) {
				System.out.println("Parameters:");
				System.out.println("1) filename: csv file with facebook ids to monitor");
				System.out.println("2) from: monitoring start date in the format dd-mm-yyyy");
				System.out.println("3) to: monitoring end date in the format dd-mm-yyyy");
				System.out.println("4) lenght: max active concurrent threads in queue");
				System.out.println("5) timeout: job's timeout in milliseconds");
				System.out.print("> ");
			}
			return s;
		} catch (IOException e) {
			e.printStackTrace();
			return getParameter();
		}
	}


	private static ArrayList<String> readInputFile(String folder) {
		ArrayList<String> output = new ArrayList<String>();
		File folderFile = new File(folder);
		if (folderFile.isDirectory()) {
			File[] listOfFiles = folderFile.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					output.add(file.getName());
					System.out.println("\t" + file.getName());
				}
			}
		}
		return output;
	}

}
