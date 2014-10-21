class ConciseForLoop {
	public static void main(String[] args) {
		for(int i = 0; i < 10; ++i) {
			final int currentNum = i;
			new Thread(new Runnable() {
				public void run() {
					System.out.println(currentNum);
				}
			}).start();
		}
	}	
}