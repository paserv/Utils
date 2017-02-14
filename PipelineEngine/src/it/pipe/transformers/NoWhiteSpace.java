package it.pipe.transformers;

import java.util.ArrayList;
import java.util.Iterator;

import it.pipe.core.Transformer;

public class NoWhiteSpace extends Transformer {

	public NoWhiteSpace() {
		super();
	}
	
	public NoWhiteSpace(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	
	@Override
	public ArrayList<String> getOutput(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		Iterator<String> iter = input.iterator();
		while (iter.hasNext()) {
			String curr = iter.next();
			String trimmed = curr.trim();
			String noWhiteSpace = trimmed.replaceAll("\\s+", " ");
			output.add(noWhiteSpace);
		}
		return output;
	}

	
	
}
