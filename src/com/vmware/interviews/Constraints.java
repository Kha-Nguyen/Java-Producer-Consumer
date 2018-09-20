package com.vmware.interviews;

class Constraints {
	static final int
			MAX_PRODUCERS = 10,
			MAX_CONSUMERS = 10,
			MIN_INT_IN_QUEUE = 1,
			MAX_INT_IN_QUEUE = 100,
			CONSUMER_SLEEP_MILLISECONDS = 100,
			PRODUCER_SLEEP_MILLISECONDS = 100,
			UPDATE_INTERVAL_MILLISECONDS = 1000,
			PRODUCER_BLOCK_MIN = 80,
			PRODUCER_BLOCK_MAX = 100;
	static final String
			OUT_FILE_NAME = "data.txt",
			CONTROL_FLOW_REGEXP = "([Qq](uit)?)?([Ee](xit)?)?([Ss](top)?)?";
}
