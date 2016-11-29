package day23network;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


public class MultiThreadServer extends Application
{ // Text area for displaying contents 
	private TextArea ta = new TextArea(); 
	private static ArrayList<PrintWriter> clientOutputStreams;
	// Number a client 
	private int clientNo = 0; 
	private List<String> clientList = new ArrayList<String>();
	private Map<String,String> userList;
	private String message;
	private ClientObservable ov = new ClientObservable();
	public static ArrayList<PrintWriter> getclientOutputStreams(){
		return clientOutputStreams;
	}
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
					//PrintWriter writer = new PrintWriter(socket.getOutputStream());
					ClientObserver writer = new ClientObserver(socket.getOutputStream());
					InetAddress inetAddress = socket.getInetAddress();
					clientOutputStreams.add(writer);
				//	userList.put(inetAddress.getHostName(), writer);
					clientNo++; 
					ov.addObserver(writer);
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
			writer.println(message);
			writer.flush();
		}
	}

	// Define the thread class for handling
	class HandleAClient extends Observable implements Runnable {
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
					setMessage(message);
					System.out.println(ov.countObservers());
					ov.setChange();
					System.out.println(ov.hasChanged());
					ov.notifyObservers(message);			
					}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	public void setMessage(String message){
		this.message = message;
	}
	public String getMessage(){
		return this.message;
	}
	public static void main(String[] args) {
		launch(args);
	}
}