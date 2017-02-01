package it.queue;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

public class Queue {

	private static Logger LOGGER = Logger.getLogger(Queue.class.getName());

	private SyncCounter counter = new SyncCounter();

	private Hashtable<String, RunnableQueue> threads;
	private Hashtable<String, Thread> runningThreads;

	private int queueLenght = 0;

	private long checkSleep;
	private long queueTimeout;
	private long startTime;

	private boolean rollback = true;
	private ThrottlingManager throttlingManager;

	private String name = "esposito";


	public Queue (int lenght, long checkSleep, long queueTimeout) {
		this.threads = new Hashtable<String, RunnableQueue>();
		this.runningThreads = new Hashtable<String, Thread>();
		this.queueLenght = lenght;
		this.checkSleep = checkSleep;
		this.queueTimeout = queueTimeout;
		//		Handler handler = new ConsoleHandler();
		//		Formatter formatter = new SimpleFormatter();
		//		handler.setFormatter(formatter);
		//		LOGGER.addHandler(handler);
	}

	public synchronized void addThread (RunnableQueue thread) {
		thread.setQueue(this);
		this.threads.put(String.valueOf(this.counter.idIncrement.incrementAndGet()), thread);
		LOGGER.info("Queue " + this.getName() + " lenght: " + this.threads.size());
	}

	public boolean executeAndWait() throws TimeoutException {
		LOGGER.info("Running Queue: " + this.getName());
		this.startTime = System.currentTimeMillis();
		while (true) {
			if (this.threads.isEmpty() && this.counter.runningThread.intValue() == 0) {
				LOGGER.info("Queue " + this.getName() + " executed correctly");
				return true;
			}
			long currentTime = System.currentTimeMillis();
			long elapsedTime = currentTime - startTime;
			if (this.queueTimeout != 0L && elapsedTime > this.queueTimeout) {
				LOGGER.severe("Queue " + this.getName() + " in timeout");
				stopQueue();
				throw new TimeoutException();
			}
			if (throttlingManager != null) {
				LOGGER.info("Throttling check");
				while (true) {
					if (!throttlingManager.canGo()) {
						try {
							Thread.sleep(throttlingManager.getPauseInterval());
							LOGGER.warning("Throttling risk... Pausing queue");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						LOGGER.info(" -----> Can Go");
						break;
					}
				}	
			}
			this.run();

//			idle();	

		}

	}

	@SuppressWarnings("unused")
	private void idle() {
		for (int i = 1; i <= 10; i++) {
			try {
				Thread.sleep(checkSleep/10);
				System.out.println(i*10 + "% for waiting to complete");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void stopQueue() {
		Enumeration<Thread> tds = this.runningThreads.elements();
		while (tds.hasMoreElements()) {
			Thread curr = tds.nextElement();
			curr.interrupt();
		}

	}

	private void run() {
		Enumeration<String> keys = this.threads.keys();
		while (keys.hasMoreElements()) {
			String currKey = keys.nextElement();
			RunnableQueue current = this.threads.get(currKey);
			if (this.counter.runningThread.intValue() < this.queueLenght) {
				Thread worker = new Thread(current);
				if (current.getName() != null) {
					worker.setName(current.getName());
				}
				worker.start();
				addRunningThread(currKey, worker);
				LOGGER.info("New thread running in Queue " + this.getName() + ": " + worker.getName());
				int runThread = this.counter.runningThread.incrementAndGet();
				LOGGER.info("Running " + runThread + " threads in Queue " + this.getName());
				this.threads.remove(currKey);
				LOGGER.info(this.threads.size() + " threads in Queue " + this.getName());
			}
		}
	}


	public Hashtable<String, RunnableQueue> getThreads() {
		return threads;
	}

	public void setThreads(Hashtable<String, RunnableQueue> threads) {
		this.threads = threads;
	}

	public SyncCounter getCounter() {
		return counter;
	}

	public void setCounter(SyncCounter counter) {
		this.counter = counter;
	}

	public boolean isRollback() {
		return rollback;
	}

	public void setRollback(boolean rollback) {
		this.rollback = rollback;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ThrottlingManager getThrottlingManager() {
		return throttlingManager;
	}

	public void setThrottlingManager(ThrottlingManager throttlingManager) {
		this.throttlingManager = throttlingManager;
	}

	public synchronized void removeRunningThread(String id) {
		this.runningThreads.remove(id);
	}

	public synchronized void addRunningThread (String currKey, Thread worker) {
		this.runningThreads.put(currKey, worker);
	}

}
