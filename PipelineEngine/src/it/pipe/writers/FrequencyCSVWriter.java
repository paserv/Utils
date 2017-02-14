package it.pipe.writers;

import it.pipe.core.Writer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FrequencyCSVWriter extends Writer {

	public FrequencyCSVWriter() {
		super();
	}
	
	public FrequencyCSVWriter(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public void write(ArrayList<String> input) {

		FileOutputStream out;
		try {
			out = new FileOutputStream("output/" + config.get("path") + "_" + System.currentTimeMillis());
			byte[] aCapo = "\n".getBytes();
			ArrayList<String> appoggio = new ArrayList<String>();

			for (int i = 0; i < input.size(); i++) {
				String curr = input.get(i);
				int occurrence = 1;
				if (!appoggio.contains(curr)) {
					for (int j = i + 1; j < input.size(); j++) {
						String secondIter = input.get(j);
						if(secondIter.equalsIgnoreCase(curr)){
							occurrence++;
							appoggio.add(secondIter);
						}
					}
					out.write(curr.getBytes());
					out.write(",".getBytes());
					String occ = String.valueOf(occurrence);
					out.write(occ.getBytes());
					out.write(aCapo);
				} 
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}