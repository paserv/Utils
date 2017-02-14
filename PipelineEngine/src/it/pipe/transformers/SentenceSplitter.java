package it.pipe.transformers;

import it.pipe.core.Transformer;

import java.util.ArrayList;
import java.util.Iterator;

public class SentenceSplitter extends Transformer {

	public SentenceSplitter() {
		super();
	}
	
	public SentenceSplitter(String modName, String conFilePath) {
		super(modName, conFilePath);
	}


	@Override
	public ArrayList<String> getOutput(ArrayList<String> input) {

		ArrayList<String> result = new ArrayList<String>();

		Iterator<String> it = input.iterator();
		while (it.hasNext()){

			String currInput = it.next();
			String[] tokens = currInput.split("\\.");
			
			for (int i = 0; i < tokens.length; i++) {
				String currSentence = tokens[i];
				result.add(currSentence);
			}



		}
		return result;
	}
}
