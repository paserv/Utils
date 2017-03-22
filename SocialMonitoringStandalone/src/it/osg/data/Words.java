package it.osg.data;

import java.util.ArrayList;

public class Words {

	private ArrayList<String> words = new ArrayList<String>();

	public synchronized void addWords (ArrayList<String> otherWords) {
		this.words.addAll(otherWords);
	}
	
	public synchronized void addWord (String otherWord) {
		this.words.add(otherWord);
	}

	public ArrayList<String> getWords() {
		return words;
	}

	public void setWords(ArrayList<String> words) {
		this.words = words;
	}
	
	
	
}
