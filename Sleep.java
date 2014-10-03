class Sleep {
	public static void main(String[] args) {
		System.out.println("Main method start");
		new Thread(new Runnable() {
			public void run() {
				System.out.println("Sleeping...");
				try {
					Thread.sleep(5*1000);
				} catch(Exception ex) {}
				System.out.println("Awake!");
			}
		}).start();
		System.out.println("Main method done");
	}
}