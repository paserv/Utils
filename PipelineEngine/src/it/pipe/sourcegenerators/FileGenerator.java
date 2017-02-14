package it.pipe.sourcegenerators;


import it.pipe.core.SourceGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileGenerator extends SourceGenerator {

	public FileGenerator() {
		super();
	}
	
	public FileGenerator(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public ArrayList<String> add() {
		ArrayList<String> result = new ArrayList<String>();
		try {
			Scanner fs = new Scanner(new File(config.get("sourcefile")));
			fs.useDelimiter("\n");
			while (fs.hasNext()) {
				result.add(fs.next());
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		return result;
	}


}
