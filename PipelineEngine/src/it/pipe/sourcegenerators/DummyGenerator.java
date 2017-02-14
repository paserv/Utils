package it.pipe.sourcegenerators;


import it.pipe.core.SourceGenerator;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DummyGenerator extends SourceGenerator {

	public DummyGenerator() {
		super();
	}
	
	public DummyGenerator(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public ArrayList<String> add() {
		ArrayList<String> result = new ArrayList<String>();
		String add = "123edimgfd32";
		byte[] array = add.getBytes();
		String transformed;
		try {
			transformed = new String(array, "UTF-8");
			result.add(transformed);
		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}


}
