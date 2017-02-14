package it.pipe.transformers;

import it.pipe.core.Transformer;

import java.util.ArrayList;
import java.util.Iterator;

public class Replacer extends Transformer {

	public Replacer() {
		super();
	}

	public Replacer(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public ArrayList<String> getOutput(ArrayList<String> input) {

		ArrayList<String> result = new ArrayList<String>();

		Iterator<String> iter = input.iterator();
		while (iter.hasNext()) {
			String curr = iter.next();
			String newStr = curr.replace(":", " ");
			result.add(newStr);
		}
		
		return result;
	}

}
