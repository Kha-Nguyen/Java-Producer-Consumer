package com.vmware.interviews;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vmware.interviews.Constraints.*;

public class Main {
	private static final Scanner stdin = new Scanner(System.in);
	private static final ArrayBlockingQueue<Integer> productionQueue = new ArrayBlockingQueue<>(PRODUCER_BLOCK_MAX);
	private static final Sentinel.productionQueueMonitor watchdog = new Sentinel.productionQueueMonitor(productionQueue);
	private static Thread observer = new Thread(watchdog);



	private static int promptInput(final int Max, String varName)
	{
		int in = 0;
		while (in <= 0 || in >= Max) {
			System.out.println("Please enter " + varName + ": (" + 0 + ", " + Max + ")");
			in = stdin.nextInt();
		}
		return in;
	}

		public static void main(String[] args)
	{


		try (BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_NAME, true))) {
			final int n = promptInput(MAX_PRODUCERS, "N"), m = promptInput(MAX_CONSUMERS, "M");
			List<IntMaker> makers = Stream.generate(() -> new IntMaker(productionQueue, watchdog)).limit(n).collect(Collectors.toList());
			List<Thread> producers = makers.stream().map(Thread::new).collect(Collectors.toList());
			producers.forEach(Thread::start);
			observer.start();
			List<IntWriter> writers = Stream.generate(() -> new IntWriter(productionQueue, outFile)).limit(m).collect(Collectors.toList());
			List<Thread> consumers = writers.stream().map(Thread::new).collect(Collectors.toList());
			consumers.forEach(Thread::start);

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					stopProgram(makers, writers, consumers);
				} catch (InterruptedException e) {
					System.out.println("Cleanup Failed. Possible data loss. ");
				}
			}));

			listenForStopCommand();
			stopProgram(makers, writers, consumers);
		} catch (IOException exc) {
			exc.printStackTrace();
		} catch (InterruptedException e){
			// Shouldn't happen
		}
	}

	private static void stopProgram(List<IntMaker> makers, List<IntWriter> writers, List<Thread> consumers) throws InterruptedException
	{
		makers.forEach(IntMaker::terminate);
		watchdog.terminate();
		observer.join();
		writers.forEach(IntWriter::terminate);
		for (Thread w : consumers) {
			w.join();
		}
	}

	private static void listenForStopCommand()
	{
		while (!stdin.hasNext(CONTROL_FLOW_REGEXP)) {
			System.out.println("Unrecognised command: try Exit/Quit/Stop");
			stdin.next();
		}
	}

}
