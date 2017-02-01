package it.queue;

import java.util.concurrent.atomic.AtomicInteger;

public class SyncCounter {
	
	public AtomicInteger runningThread = new AtomicInteger(0);
	public AtomicInteger idIncrement = new AtomicInteger(0);
	
	

}
