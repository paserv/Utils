package it.pipe.writers;

import it.pipe.core.Writer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class SimpleCSVWriter extends Writer {

	public SimpleCSVWriter() {
		super();
	}
	
	public SimpleCSVWriter(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public void write(ArrayList<String> input) {
		
		try {
			FileOutputStream out = new FileOutputStream(config.get("path") + "output_" + System.currentTimeMillis() + ".txt");
			Iterator<String> iter = input.iterator();
			byte[] sep = (" ").getBytes();
			String separator = config.get("separator");
			if (separator.equals("#LF")) {
				sep = ("\n").getBytes();
			}
			while (iter.hasNext()){
				String curr = iter.next();
				out.write(curr.getBytes());
				out.write(sep);
			}
			out.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
