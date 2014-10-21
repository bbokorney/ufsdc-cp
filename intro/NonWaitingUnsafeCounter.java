class NonWaitingUnsafeCounter {
	
	static int counter;

	static void increment() {
		counter++;
	}

	public static void main(String[] args) throws InterruptedException {
		int numThreads = Integer.parseInt(args[0]);
		final int numIncrements = Integer.parseInt(args[1]);
		for(int i = 0; i < numThreads; ++i) {
			final int currentNum = i;
			new Thread(new Runnable() {
				public void run() {
					for(int i = 0; i < numIncrements	; ++i) {
						increment();
					}
				}
			}).start();
		}

		System.out.println(counter);
	}

}