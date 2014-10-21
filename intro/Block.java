import java.util.Scanner;

class Main {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.print("What's your name? ");
		String name = in.nextLine();
		System.out.println("Nice to meet you " + name + "!");
	}
}