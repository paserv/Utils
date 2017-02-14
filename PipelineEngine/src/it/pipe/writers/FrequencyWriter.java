package it.pipe.writers;

import it.pipe.core.Writer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class FrequencyWriter extends Writer {

	public FrequencyWriter() {
		super();
	}

	public FrequencyWriter(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public void write(ArrayList<String> input) {

		FileOutputStream out;
		try {
			out = new FileOutputStream(config.get("path"));
			int max = Integer.valueOf(config.get("max"));
			String separator = config.get("separator");

			byte[] aCapo = "\n".getBytes();

			Multiset<String> result = HashMultiset.create();

			Iterator<String> iter = input.iterator();
			while (iter.hasNext()) {
				String curr = iter.next();
				result.add(curr);
			}

			List<Entry<String>> sortedHt = sortMultisetPerEntryCount(result);
			int topHTNumber;
			if (sortedHt.size() < max) {
				topHTNumber = sortedHt.size();
			} else {
				topHTNumber = max;
			}

			List<Entry<String>> topHunHT = sortedHt.subList(0, topHTNumber);
			for (int i = 0; i < topHunHT.size(); i++) {
				try {
					out.write(topHunHT.get(i).getElement().getBytes());
					out.write(separator.getBytes());
					out.write(String.valueOf(topHunHT.get(i).getCount()).getBytes());
					out.write(aCapo);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			out.close();


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	
	private static <T> List<Entry<T>> sortMultisetPerEntryCount(Multiset<T> multiset) {
		Comparator<Multiset.Entry<T>> occurence_comparator = new Comparator<Multiset.Entry<T>>() {
			public int compare(Multiset.Entry<T> e1, Multiset.Entry<T> e2) {
				return e2.getCount() - e1.getCount();
			}
		};
		List<Entry<T>> sortedByCount = new ArrayList<Entry<T>>(multiset.entrySet());
		Collections.sort(sortedByCount, occurence_comparator);

		return sortedByCount;
	}
}