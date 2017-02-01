package test;

import java.util.logging.Level;
import it.queue.Queue;
import it.queue.QueueLogger;
import it.queue.TimeoutException;

public class Test {

	public static void main(String[] args) {

		QueueLogger.setup(Level.FINE);
		
		SimpleSyncCounter count = new SimpleSyncCounter();
		Queue queue = new Queue(10, 1000L, 100000L);
		queue.setName("PincoPallino");
				
		for (int i = 0; i < 100; i++) {
			RunnableQueueImpl cur = new  RunnableQueueImpl(count);
			cur.setName("ASSA");
			queue.addThread(cur);
		}
		
		SimpleSyncCounter count2 = new SimpleSyncCounter();
		Queue queue2 = new Queue(10, 1000L, 100000L);
		queue2.setName("Abdulla");
				
		for (int i = 0; i < 100; i++) {
			RunnableQueueImpl cur = new  RunnableQueueImpl(count2);
			cur.setName("ASSA");
			queue2.addThread(cur);
		}
		
		
		try {
			queue.executeAndWait();
			queue2.executeAndWait();
			
			System.out.println(count.count);
			
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

}
