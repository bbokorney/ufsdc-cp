class NestedForLoops {
	public static void main(String[] args) {
		for(int i = 0; i < 3; ++i) {
			new Thread(new Runnable() {
				public void run() {
					for(int i = 0; i < 5; ++i) {
						System.out.println("["+Thread.currentThread().getName()+"] "+i);
					}
				}
			}).start();
		}
	}
}