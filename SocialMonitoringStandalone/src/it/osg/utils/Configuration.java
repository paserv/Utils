package it.osg.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class Configuration {

	private static Logger LOGGER = Logger.getLogger(Configuration.class.getName());
	private static final String CONF_PATH = "conf/conf.properties";

	public static long ROLLBACK_SLEEP;
	public static long QUEUE_CHECK_SLEEP;

	public static long SLIDING_WINDOW;
	public static int MAX_REQUEST;
	public static long THROTTLING_SLEEP;
	public static String OUTPUT_FOLDER;

	static {
		LOGGER.info("Getting Configuration");
		getConfiguration();
	}

	public static void getConfiguration() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(CONF_PATH);
			prop.load(input);
			ROLLBACK_SLEEP = Long.valueOf(prop.getProperty("ROLLBACK_SLEEP"));
			QUEUE_CHECK_SLEEP = Long.valueOf(prop.getProperty("QUEUE_CHECK_SLEEP"));
			SLIDING_WINDOW = Long.valueOf(prop.getProperty("SLIDING_WINDOW"));
			MAX_REQUEST = Integer.valueOf(prop.getProperty("MAX_REQUEST"));
			THROTTLING_SLEEP = Long.valueOf(prop.getProperty("THROTTLING_SLEEP"));
			OUTPUT_FOLDER = prop.getProperty("OUTPUT_FOLDER");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


	}

}
