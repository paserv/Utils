package it.queue;

public interface ThrottlingManager {

	public long getPauseInterval();
	public boolean canGo();
	
}
