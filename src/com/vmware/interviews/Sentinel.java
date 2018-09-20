package com.vmware.interviews;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import static com.vmware.interviews.Constraints.*;

public interface Sentinel {
	void subscribe(IntMaker sub);

	@SuppressWarnings("unused")
	void unsubscribe(IntMaker sub);

	@SuppressWarnings("unused")
	void updateSubscribers();

	abstract class WatchDog implements Sentinel {
		ArrayList<IntMaker> subscribers_ = new ArrayList<>();

		@Override
		synchronized public void subscribe(IntMaker sub) { subscribers_.add(sub); }

		@Override
		synchronized public void unsubscribe(IntMaker sub) { subscribers_.remove(sub); }
	}

	class productionQueueMonitor extends WatchDog implements Runnable {
		private boolean terminate_ = false;
		private BlockingQueue<Integer> productionQueue_;
		private int numberOfWakesPerUpdate=10;

		productionQueueMonitor(BlockingQueue<Integer> productionQueue) { productionQueue_ = productionQueue; }

		@Override
		public void updateSubscribers()
		{
			subscribers_.stream()
					.filter(IntMaker::blocked)
					.forEach(IntMaker::unblock);
		}

		synchronized void terminate()
		{
			this.terminate_ = true;
		}

		@Override
		public void run()
		{
			terminate_ = false;
			while (!terminate_) {
				try {
					for (int i = 0; i < numberOfWakesPerUpdate; i++) {
						if (productionQueue_.size() <= PRODUCER_BLOCK_MIN)
							updateSubscribers();
						Thread.sleep(UPDATE_INTERVAL_MILLISECONDS/numberOfWakesPerUpdate);
					}
					long activeSubscribers = subscribers_.stream().filter(sub -> !sub.blocked()).count();
					System.out.println(productionQueue_.size() + " P: " + activeSubscribers + " / " + subscribers_.size());
				} catch (InterruptedException e) {
					//Stop sleeping
				}
			}
			System.out.println("Stopping Watchdog.");

		}

	}
}
