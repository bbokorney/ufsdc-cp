import java.net.*;
import java.io.*;
import java.util.*;

class Client {
	
	void runClient() throws Exception {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter your username: ");
		String name = in.nextLine();
		connectToServer();
		sendMessage(name);
		while(true) {
			System.out.print("> ");
			String message = in.nextLine();
			sendMessage(message);
		}
	}

	public static void main(String[] args) {
		try {
			address = args[0];
			port = Integer.parseInt(args[1]);
			System.out.println(address);
			new Client().runClient();
		} catch (Exception e) {
			System.err.println("Something went wrong!");
			System.err.println(e.toString());
		}
	}


	// Beware! Networking stuff!
	static String address;
	static int port;
	Socket socket;
	PrintWriter output;

	void connectToServer() throws Exception {
		socket = new Socket(address, port);
		output = new PrintWriter(socket.getOutputStream(), true);
	}

	void sendMessage(String message) {
		output.println(message);
	}
}