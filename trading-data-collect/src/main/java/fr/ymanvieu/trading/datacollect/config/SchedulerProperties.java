package fr.ymanvieu.trading.datacollect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("trading.scheduler")
public class SchedulerProperties {
    
    enum SchedulerType {
        
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
        RANDOM,
        
        /**
         * None (disable scheduler)
         */
        NONE;
    }

	/**
	 * Scheduling type for data collect.
	 * Empty/{none} to disable scheduling (default).
	 */
	private SchedulerType type = SchedulerType.NONE;
	
	
	public SchedulerType getType() {
		return type;
	}
	
	public void setType(SchedulerType type) {
		this.type = type;
	}
}
