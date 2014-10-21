import java.net.*;
import java.io.*;
import java.util.*;

class MultiThreadServer {
	
	void runServer() throws Exception {
		startListen();
		while(true) {
			ChatClient newClient = waitForNewClient();
			new Thread(new ClientHandler(newClient)).start();
		}	
	}

	class ClientHandler implements Runnable {
		ChatClient client;

		ClientHandler(ChatClient client) {
			this.client = client;
		}

		public void run() {
			System.out.println("New user " + client.name + " has connected.");
			while(true) {
				try {
					String message = client.waitForNextMessage();
					System.out.println(client.name + ": " + message);
				} catch (Exception e) {
					System.err.println("Error receiving message from client " + client.name);
					System.err.println(e.toString());
				}
			}
		}
	}


	// Beware! Networking stuff!
	ServerSocket server;
	static int portNum;

	public static void main(String[] args) {
		try {
			portNum = Integer.parseInt(args[0]);
			new MultiThreadServer().runServer();
		} catch (Exception e) {
			System.err.println("Something went wrong!");
			System.err.println(e.toString());
		}
	}
	
	void startListen() throws Exception {
		System.out.println("Listening on port " + portNum + "...");
		server = new ServerSocket(portNum);
	}

	ChatClient waitForNewClient() throws Exception {
		Socket client = server.accept();
		Scanner input = new Scanner(client.getInputStream());
		String name = input.nextLine();
		return new ChatClient(name, input);
	}
	class ChatClient {
		String name;
		Scanner input;
		
		ChatClient(String name, Scanner input) {
			this.name = name;
			this.input = input;
		}

		String waitForNextMessage() throws Exception {
			return input.nextLine();
		}
	}
}

