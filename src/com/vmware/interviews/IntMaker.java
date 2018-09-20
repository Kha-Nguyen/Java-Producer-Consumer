package com.vmware.interviews;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import static com.vmware.interviews.Constraints.*;


class IntMaker implements Runnable {
	private boolean blocked_ = false;
	private boolean terminate_ = false;
	private ArrayBlockingQueue<Integer> out_;

	boolean blocked() { return blocked_;}

	IntMaker(ArrayBlockingQueue<Integer> out, Sentinel sentinel)
	{
		out_ = out;
		sentinel.subscribe(this);
	}

	private void addWorkToQueue() throws InterruptedException
	{
		if (out_.offer(ThreadLocalRandom.current().nextInt(MIN_INT_IN_QUEUE, MAX_INT_IN_QUEUE))) {
			Thread.sleep(ThreadLocalRandom.current().nextInt(1, PRODUCER_SLEEP_MILLISECONDS));
		} else {
			blocked_ = true;
			synchronized (this) { // Quirk of the language, shouldn't be necessary, but is.
				while (blocked_)
					wait();
			}
		}

	}

	synchronized void terminate() { terminate_ = true; notify(); }
	synchronized void unblock() { blocked_ = false; notify(); }


	@Override
	public void run()
	{
		terminate_ = false;
		try {
			while (!terminate_)
				this.addWorkToQueue();
		} catch (InterruptedException exc) {
			System.out.println("Interrupting Producer Thread.");
		} finally {
			System.out.println("Terminating Producer. ");
		}
	}
}
