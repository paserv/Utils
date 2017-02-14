package it.pipe.filters;

import it.pipe.core.Filter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import au.com.bytecode.opencsv.CSVReader;

public class RemoveWordList extends Filter {

	public RemoveWordList() {
		super();
	}
	
	public RemoveWordList(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public ArrayList<String> getOutput(ArrayList<String> input) {

		ArrayList<String> result = new ArrayList<String>();
		Hashtable<String, String> wordList = this.getWordList();

		for (int i = 0; i < input.size(); i++){
			String currToken = input.get(i);

			String vocabMatch = wordList.get(currToken.toLowerCase());
			if (vocabMatch == null) {
				result.add(currToken);
			}

		}
		return result;
	}


	private Hashtable<String, String> getWordList() {
		Hashtable<String, String> vocabularyMap = new Hashtable<String, String>();

		Collection<String> keys = config.values();
		Iterator<String> iter = keys.iterator();
		while(iter.hasNext()){
			String currValue = iter.next();

			try {

				CSVReader reader = new CSVReader(new FileReader(currValue), ';');
				List<String[]> rows = reader.readAll();
				reader.close();
				
				Iterator<String[]> iterRows = rows.iterator();
				while (iterRows.hasNext()) {
					String[] currRow = iterRows.next();
					String currTerm = currRow[0];
					String[] spl = currTerm.split(" ");
					vocabularyMap.put(spl[0].toLowerCase(), spl[0].toLowerCase());
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return vocabularyMap;
	}


}
