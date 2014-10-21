import java.util.Scanner;

class BlockAndSleep {
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		System.out.print("How many seconds should I sleep for? ");
		int sleepTime = in.nextInt();
		System.out.println("Sleeping for " + sleepTime + " seconds...");
		Thread.sleep(sleepTime * 1000);
		System.out.println("Nap time over!");
	}
}