package it.osg.data;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class Hashtags {
	private Multiset<String> resultHashtags = HashMultiset.create();

	public synchronized void addToMultiset(String string) {
		this.resultHashtags.add(string);
	}

	public synchronized void addToMultiset(String[] vector) {
		for (int j = 1; j < vector.length; j++) {
			this.resultHashtags.add(vector[j]);
		}
	}

	public synchronized void addToMultiset(List<String> list) {
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) {
			this.resultHashtags.add(iter.next());
		}
	}

	public Multiset<String> getResultHashtags() {
		return resultHashtags;
	}
	
}
