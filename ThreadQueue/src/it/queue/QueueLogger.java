package it.queue;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

public class QueueLogger {

	public static void setup(Level level) {
		LogManager.getLogManager().getLogger("").setLevel(level);
		Handler[] handlers = LogManager.getLogManager().getLogger("").getHandlers();
		for ( int index = 0; index < handlers.length; index++ ) {
			handlers[index].setLevel(level);
		}
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler(QueueLogger.class.getName() + ".log", true);
			SimpleFormatter formatter = new SimpleFormatter();  
			fileHandler.setFormatter(formatter); 
			LogManager.getLogManager().getLogger("").addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
