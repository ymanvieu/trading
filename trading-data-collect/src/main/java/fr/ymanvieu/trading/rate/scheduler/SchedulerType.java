package fr.ymanvieu.trading.rate.scheduler;

public enum SchedulerType {
	
	/**
	 *  Cron-based
	 */
	CRON,
	
	/**
	 * Fixed rate 
	 */
	FIXED_RATE,
	
	/**
	 * Randomly-generated data for tests
	 */
	RANDOM;
}
