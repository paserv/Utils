package test;

import it.queue.RunnableQueue;

public class RunnableQueueImpl extends RunnableQueue {

	private SimpleSyncCounter count;
	private boolean incrementExecuted;
	
	public RunnableQueueImpl(SimpleSyncCounter count) {
		super();
		this.count = count;
	}
	
	@Override
	public void executeJob() throws Exception {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.count.count.incrementAndGet();
		incrementExecuted = true;
		try {
			if (this.count.count.intValue() == 1) {
				Integer.valueOf("assa");
			}
		} catch (Exception e) {
			System.out.println("Eccezione");
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void rollback() {
		System.out.println("RollBack");
		if (incrementExecuted) {
			this.count.count.decrementAndGet();
		}
		
	}

}
