package it.osg.data;

import java.util.Iterator;
import java.util.logging.Logger;

import com.google.common.collect.BoundType;
import com.google.common.collect.TreeMultiset;

public class RequestCounter {


	private TreeMultiset<Long> requests = TreeMultiset.create();

	private static Logger LOGGER = Logger.getLogger(RequestCounter.class
			.getName());

	public synchronized void addRequest(long timestamp) {
		requests.add(timestamp);
	}

	public synchronized int greaterThanNumber(long number) {
		requests = (TreeMultiset<Long>) requests.tailMultiset(number,
				BoundType.CLOSED);
		return requests.size();
	}

	public synchronized void print() {
		Iterator<Long> iter = requests.iterator();
		while (iter.hasNext()) {
			Long curr = iter.next();
			LOGGER.info(curr.toString());
		}
	}

	public static void main(String[] args) {
		RequestCounter counter = new RequestCounter();
		counter.addRequest(1401271548593L);
		counter.addRequest(1401271550620L);
		counter.addRequest(1401271550620L);
		counter.addRequest(453L);
		counter.addRequest(454L);
		counter.addRequest(454L);
		counter.addRequest(454L);
		counter.addRequest(454L);
		counter.addRequest(4541L);

		System.out.println(counter.greaterThanNumber(1401271589785L));

		System.out.println(counter);

	}

}
