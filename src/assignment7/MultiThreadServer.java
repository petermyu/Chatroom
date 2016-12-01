package assignment7;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


public class MultiThreadServer extends Application
{ // Text area for displaying contents 
	private TextArea ta = new TextArea(); 
	static ArrayList<PrintWriter> clientOutputStreams;
	// Number a client 
	private int clientNo; 
	
	private List<String> clientList = new ArrayList<String>();
	private Map<Integer,String> userMap = new HashMap<Integer,String>();
	private String message;
	private List<String> usernameList = new ArrayList<String>();
	private ArrayList<String> activeUserList = new ArrayList<String>();
	static ClientObservable ov = new ClientObservable();
	static ClientObservable ovCI = new ClientObservable();
	static UserListObservable ovUser = new UserListObservable();
	static HashMap<String,UserListObservable> observableMap = new HashMap<String,UserListObservable>();
	static HashMap<String,PrintWriter> writerMap = new HashMap<String,PrintWriter>();
	private static HashMap<ArrayList<String>,Integer> chatMap = new HashMap<ArrayList<String>,Integer>();
//	static UserListWriter userWriter = new UserListWriter(new File("src/").getParent());
	public static ArrayList<String> parseSelectedList(BufferedReader reader){
		String userLong;
		ArrayList<String> list = new ArrayList<String>();
		try {
			userLong = reader.readLine();
			String[] users = userLong.split(" ");
			for(String user : users){
				list.add(user);
			}
			
			return list;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	public int getClientInfo(ArrayList<String> selectedList, ClientObserver infoWriter){
		Collections.sort(selectedList);
		for(ArrayList<String> compare : chatMap.keySet()){
			Collections.sort(compare);
			if(compare.equals(selectedList)){
				System.out.println("clients :" + clientNo);
				infoWriter.println(chatMap.get(selectedList));
				infoWriter.flush();
				return chatMap.get(compare);
			}
		}
		chatMap.put(selectedList, clientNo);
		infoWriter.println(clientNo);
		infoWriter.flush();
	//	UserListWriter cW = new UserListWriter(ChatClient.path,"client_info");
		
	//	cW.updateClientText(Integer.toString(clientNo));
		
		clientNo++;
		System.out.println("clients : " + clientNo);
		return chatMap.get(selectedList);
		
	}
	public void setUserList(String path,PrintWriter writer){
		UserListWriter newWriter = new UserListWriter(path,"whitelist");
		ArrayList<String> users = newWriter.readUsers();
		for(String name : users){
			if(!usernameList.contains(name)){
				usernameList.add(name);
				writerMap.put(name, writer);
			}
		}
	}
	@Override // Override the start method in the Application class 
	public void start(Stage primaryStage) { 
		// Create a scene and place it in the stage 
		Scene scene = new Scene(new ScrollPane(ta), 450, 200); 
		primaryStage.setTitle("MultiThreadServer"); // Set the stage title 
		primaryStage.setScene(scene); // Place the scene in the stage 
		primaryStage.show(); // Display the stage 
		/*ServerTask newConnection = new ServerTask();
		try {
			new Thread(new HandleAClient(newConnection.getAddress(),newConnection.getSocket()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(newConnection).start();*/
		
		
		new Thread( () -> { 
			try {  // Create a server socket 
				ServerSocket serverSocket = new ServerSocket(8000); 
				ta.appendText("MultiThreadServer started at " 
						+ new Date() + '\n'); 
				clientOutputStreams = new ArrayList<PrintWriter>();
				while (true) { 
					// Listen for a new connection request 
					Socket socket = serverSocket.accept(); 
					Socket infoSocket = serverSocket.accept();
					//PrintWriter writer = new PrintWriter(socket.getOutputStream());
					ClientObserver writer = new ClientObserver(socket.getOutputStream());
					ClientObserver infoWriter = new ClientObserver(infoSocket.getOutputStream());
					InetAddress inetAddress = socket.getInetAddress();
					setUserList(ChatClient.path,writer);
			//		setWriterMap();
					clientOutputStreams.add(writer);
					ov.addObserver(writer);
					ovCI.addObserver(infoWriter);
					BufferedReader infoReader = new BufferedReader(new InputStreamReader(infoSocket.getInputStream()));
					int clientinfo = getClientInfo(parseSelectedList(infoReader),infoWriter);
					System.out.println("server received client: " + clientinfo);
				/*	UserListWriter newWriter = new UserListWriter(ChatClient.path,"selected_list");
					ArrayList<String> list = newWriter.readUsers();
					UserListWriter cWriter = new UserListWriter(ChatClient.path, "client_info");
					cWriter.updateClientText(Integer.toString(clientinfo));*/
					//activeUserList = ChatClient.usernameList;
					System.out.println("active users:" + activeUserList.size());
					// Create and start a new thread for the connection
					new Thread(new HandleAClient(inetAddress.getHostName(),socket,infoSocket)).start();
				}
			} 
			catch(IOException ex) { 
				System.err.println(ex);
			}
		}).start();
	}
	public UserListObservable getObservable(String key){
		return null;
	}
	// Define the thread class for handling
	class HandleAClient extends Observable implements Runnable {
		private Socket socket; // A connected socket
		private BufferedReader reader;
		private BufferedReader infoReader;
		private boolean flag;
		private String hostname;
		/** Construct a thread */ 
		public HandleAClient(String hostname, Socket socket, Socket infoSocket) throws IOException { 
			this.socket = socket;
			this.flag = false;
			this.hostname = hostname;
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			infoReader = new BufferedReader(new InputStreamReader(infoSocket.getInputStream()));
		}
		/** Run a thread */
		public void run() { 
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("read " + message);
					System.out.println(ov.countObservers());
					ov.setChange();
					ov.notifyObservers(message);
					message = infoReader.readLine();
						System.out.println("readinfo " + message);
						ovCI.setChange();
						ovCI.notifyObservers(message);
					
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