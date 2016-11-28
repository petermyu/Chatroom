package day23network;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


public class MultiThreadServer extends Application
{ // Text area for displaying contents 
	private TextArea ta = new TextArea(); 
	private ArrayList<PrintWriter> clientOutputStreams;
	// Number a client 
	private int clientNo = 0; 
	private List<String> clientList = new ArrayList<String>();
	private Map<String,String> userList;
	@Override // Override the start method in the Application class 
	public void start(Stage primaryStage) { 
		// Create a scene and place it in the stage 
		Scene scene = new Scene(new ScrollPane(ta), 450, 200); 
		primaryStage.setTitle("MultiThreadServer"); // Set the stage title 
		primaryStage.setScene(scene); // Place the scene in the stage 
		primaryStage.show(); // Display the stage 

		new Thread( () -> { 
			try {  // Create a server socket 
				ServerSocket serverSocket = new ServerSocket(8000); 
				ta.appendText("MultiThreadServer started at " 
						+ new Date() + '\n'); 
				clientOutputStreams = new ArrayList<PrintWriter>();
				userList = new HashMap<String,String>();
				while (true) { 
					// Listen for a new connection request 
					Socket socket = serverSocket.accept(); 
					PrintWriter writer = new PrintWriter(socket.getOutputStream());
					InetAddress inetAddress = socket.getInetAddress();
					clientOutputStreams.add(writer);
				//	userList.put(inetAddress.getHostName(), writer);
					clientNo++; 
					
					
					
				/*	Platform.runLater( () -> { 
						// Display the client number 
						ta.appendText("Starting thread for client " + clientNo +
								" at " + new Date() + '\n'); 

						// Find the client's host name, and IP address 
						InetAddress inetAddress = socket.getInetAddress();
						ta.appendText("Client " + clientNo + "'s host name is "
								+ inetAddress.getHostName() + "\n");
						ta.appendText("Client " + clientNo + "'s IP Address is " 
								+ inetAddress.getHostAddress() + "\n");
						clientList.add(inetAddress.getHostAddress());
					}); */
						

					// Create and start a new thread for the connection
					new Thread(new HandleAClient(inetAddress.getHostName(),socket)).start();
				} 
			} 
			catch(IOException ex) { 
				System.err.println(ex);
			}
		}).start();
	}
	private void notifyClients(String message) {
		for (PrintWriter writer : clientOutputStreams) {
			String user = userList.get(writer);
			writer.println(message);
			if(message == "null"){
				System.out.println("error in " + user);
			}
			writer.flush();
			System.out.println("written");
			
		}
	}

	// Define the thread class for handling
	class HandleAClient implements Runnable {
		private Socket socket; // A connected socket
		private BufferedReader reader;
		private boolean flag;
		private String hostname;
		/** Construct a thread */ 
		public HandleAClient(String hostname, Socket socket) throws IOException { 
			this.socket = socket;
			this.flag = false;
			this.hostname = hostname;
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		/** Run a thread */
		public void run() { 
			
			
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("read " + message);
					notifyClients(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}