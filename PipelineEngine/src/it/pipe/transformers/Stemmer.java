package it.pipe.transformers;

import java.util.ArrayList;
import java.util.Iterator;

import org.tartarus.snowball.ext.ItalianStemmer;

import it.pipe.core.Transformer;

public class Stemmer extends Transformer {

	public Stemmer() {
		super();
	}
	
	public Stemmer(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public ArrayList<String> getOutput(ArrayList<String> input) {

		ItalianStemmer stemmer = new ItalianStemmer();

		ArrayList<String> output = new ArrayList<String>();
		Iterator<String> iter = input.iterator();
		while (iter.hasNext()) {
			String curr = iter.next();
			String currOut = "";
			String[] tokens = curr.split(" ");
			for (String string : tokens) {
				stemmer.setCurrent(string);
				stemmer.stem();
				String stemmed = stemmer.getCurrent();
				currOut = currOut + stemmed + " ";
			}

			output.add(currOut.trim());

		}
		return output;
	}

	

}
