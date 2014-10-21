class VerboseForLoop {
	public static void main(String[] args) {
		for(int i = 0; i < 10; ++i) {
			NumPrinter np = new NumPrinter(i);
			Thread t = new Thread(np);
			t.start();
		}
	}	
}

class NumPrinter implements Runnable {
	int num;
	public NumPrinter(int num) {
		this.num = num;
	}
	public void run() {
		System.out.println(num);
	}
}