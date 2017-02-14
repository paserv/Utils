package it.pipe.filters;

import it.pipe.core.Filter;

import java.util.ArrayList;

public class RemoveDuplicate extends Filter {

	public RemoveDuplicate() {
		super();
	}
	
	public RemoveDuplicate(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public ArrayList<String> getOutput(ArrayList<String> input) {
		int size = input.size();
		int duplicates = 0;

		for (int i = 0; i < size - 1; i++) {

			for (int j = i + 1; j < size; j++) {
				if (!input.get(j).equalsIgnoreCase(input.get(i)))
					continue;
				duplicates++;
				input.remove(j);
				j--;
				size--;
			}
		}
		return input;
	}

	
}
