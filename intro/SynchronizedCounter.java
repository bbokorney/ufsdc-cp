class SynchronizedCounter {
	
	static int counter;

	static synchronized void increment() {
		counter++;
	}

	public static void main(String[] args) throws InterruptedException {
		int numThreads = Integer.parseInt(args[0]);
		final int numIncrements = Integer.parseInt(args[1]);
		Thread[] threads = new Thread[numThreads];
		for(int i = 0; i < numThreads; ++i) {
			final int currentNum = i;
			threads[i] = new Thread(new Runnable() {
				public void run() {
					for(int i = 0; i < numIncrements; ++i) {
						increment();
					}
				}
			});
			threads[i].start();
		}

		for(int i = 0; i < threads.length; ++i) {
			threads[i].join();
		}

		System.out.println(counter);

	}

}