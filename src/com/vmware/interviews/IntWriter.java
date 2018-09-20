package com.vmware.interviews;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import static com.vmware.interviews.Constraints.*;

class IntWriter implements Runnable {  // Less coupled than extends thread.
	private boolean terminate_ = false;
	private ArrayBlockingQueue<Integer> in_;
	private BufferedWriter fout_;

	IntWriter(ArrayBlockingQueue<Integer> in, BufferedWriter fout)
	{
		in_ = in;
		fout_ = fout;
	}

	private void writeToFile(Integer number)
	{
		try {
			fout_.write(number.toString() + ", ");
			Thread.sleep(ThreadLocalRandom.current().nextInt(1, CONSUMER_SLEEP_MILLISECONDS));
		} catch (IOException exc) {
			exc.printStackTrace();
		} catch (InterruptedException exc) {
			System.out.print("Sleep interrupted. ");
		}

	}

	synchronized void terminate() { terminate_ = true;}

	@Override
	public void run()
	{
		try {
			while (!terminate_) {
				while (!in_.isEmpty()) {
					Integer number = in_.take();
					writeToFile(number);
				}
			}
			System.out.println("Terminating Consumer.");
		} catch (InterruptedException exc) {
			System.out.println("Writer Interrupted!");
		}
	}
}