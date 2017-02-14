package it.pipe.transformers;

import it.pipe.core.Transformer;
import java.util.ArrayList;

public class FrequencyTransformer extends Transformer {

	public FrequencyTransformer() {
		super();
	}
	
	public FrequencyTransformer(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public ArrayList<String> getOutput(ArrayList<String> input) {
		ArrayList<String> result = new ArrayList<String>();
		
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
				String currOccurrence = curr + "," + String.valueOf(occurrence);
				result.add(currOccurrence);
			}
		}
		return result;
	}
	
}