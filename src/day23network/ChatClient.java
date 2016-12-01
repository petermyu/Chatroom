package day23network;

import java.io.*;
import java.net.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.*;

public class ChatClient extends Application{
	private BufferedReader reader;
	private PrintWriter writer;
	private BufferedReader infoReader;
	private PrintWriter infoWriter;
	private static String chatText = "";
	private String username;
	
	static String path = new File("Chatroom/src/").getParent();
	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
	static UserListObservable userObservable = new UserListObservable();
	static ListView<String> activeList = new ListView<String>();
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		initView(primaryStage);
		
	}

	private void initView(Stage primaryStage) {
		GridPane grid = new GridPane();
		TextField userName = new TextField();
		TextField password = new TextField();
		Text user = new Text();
		Text pword = new Text();
		Button login = new Button();
		Button register = new Button();
		
		user.setText("Username : ");
		pword.setText("Password : ");
		password.setDisable(true);
		
		login.setText("Login");
		register.setText("Register");
		
		login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	username = userName.getText();
            	
            //	writer.println(username);
            //	writer.flush();

            	chatSelectPane(primaryStage);
            }
        });
		
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(userName, 2, 0);
		grid.add(password, 2, 3);
		grid.add(user, 1, 0);
		grid.add(pword, 1, 3);
		grid.add(login, 2, 4);
		grid.add(register, 3, 4);
		Scene scene = new Scene(grid, 500,500);
		primaryStage.setScene(scene);
		primaryStage.show();
/*		try {
			setUpNetworking();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	private void chatSelectPane(Stage primaryStage){
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		activeList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		UserListWriter userWriter = new UserListWriter(path,"whitelist");
		UserListObserver observer = new UserListObserver(path);
		userWriter.addUser(username);
		MultiThreadServer.ovUser.addObserver(observer);
		MultiThreadServer.ovUser.setChange();
		Button createChat = new Button();
		Button refresh = new Button();
		refresh.setText("Refresh");
		createChat.setText("Create Chat");
		
		createChat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	ObservableList<String> selectedList = FXCollections.observableArrayList();
            	selectedList = activeList.getSelectionModel().getSelectedItems();
    			ArrayList<String> findList = new ArrayList<String>(selectedList);
    			UserListWriter newWriter = new UserListWriter(ChatClient.path, "selected_list");
    			newWriter.addArrayList(findList);

            	try {
        			setUpNetworking();
        			TimeUnit.MILLISECONDS.sleep(200);
        			GroupChat newChat = new GroupChat(new ArrayList<String>(selectedList), writer, reader, username, infoWriter);
        			newChat.start(new Stage());
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}

            //	setGroupChatPane(selectedList);
            	
            }
        });
		refresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
        		UserListWriter newList = new UserListWriter(path, "whitelist");
        		ArrayList<String> userList = newList.readUsers();
        		activeList.setItems(FXCollections.observableArrayList(userList));
            }
        });
		grid.add(refresh, 1	, 2);
		grid.add(activeList, 1, 1);
		grid.add(createChat, 2, 2);
		Scene scene = new Scene(grid,800,400);
		primaryStage.setScene(scene);
		primaryStage.show();
		Runnable listUpdater = new UserListTask();
		new Thread(listUpdater).start();

		
	}
	private void setGroupChatPane(ObservableList<String> selectedList){
		Stage newStage = new Stage();
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		TextArea chatField = new TextArea();
		TextField inputMessage = new TextField();
		ArrayList<String> findList = new ArrayList<String>(selectedList);
		UserListWriter newWriter = new UserListWriter(path, "selected_list");
		newWriter.addArrayList(findList);
   		try {
			setUpNetworking();
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		UserListWriter cWriter = new UserListWriter(path, "client_info");
		String client = cWriter.readString();
	//	int clientinfo =  cWriter.readString();
		System.out.println(client);
		int clientinfo = Integer.parseInt(client);
		System.out.println("Client info: " +clientinfo);
		inputMessage.setUserData(clientinfo);
   		
		chatField.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		chatField.setText("");
		chatField.setUserData(clientinfo);
		Button send = new Button();
		send.setText("Send");
		grid.add(chatField, 1, 0);
		grid.add(inputMessage, 1, 1);
		grid.add(send, 2, 1);
		Scene scene = new Scene(grid,800,400);
		newStage.setScene(scene);
		newStage.show();
		
		send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(inputMessage.getText() != null){
            		String time = "[" + sdf.format(new Timestamp(new Date().getTime())) + "]";
	            	writer.println(username + " > " +inputMessage.getText());
	            	System.out.println(username);
	        		writer.flush();
	        		inputMessage.clear();
            	}
            }
        });
		inputMessage.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	        	if(inputMessage.getText() != null){
		        	if (ke.getCode() == KeyCode.ENTER)  {
		        	String time = "[" + sdf.format(new Timestamp(new Date().getTime())) + "]";
		        	writer.println(username + " " + time + " "+ " > " + inputMessage.getText());
	        		writer.flush();
	        		System.out.println("sending client: " + inputMessage.getUserData());
	        		infoWriter.println(inputMessage.getUserData());
	        		infoWriter.flush();
	        		inputMessage.clear();
		        	}
	        	}
	        }
	    });
		Task task = new Task<Void>() {
		    @Override public Void call() {
		    	String response;
		    	String clientinfo;
				try {
					synchronized(reader){
					while ((response = reader.readLine()) != null) {
						System.out.println("received " + response);
						UserListWriter cWriter = new UserListWriter(path, "client_info");
						clientinfo  = cWriter.readString();
							System.out.println("received CI: " + clientinfo);
							System.out.println("userdata : " +chatField.getUserData());
							int userData = (int) chatField.getUserData();
							if(userData == Integer.parseInt(clientinfo)){
							chatField.appendText(response + "\n");
							}
						
						
						
					}
					/*if(inputMessage.getText() == null){
						send.setDisable(true);
					}
					else{
						send.setDisable(false);
					}*/
				} 
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
		      
		    }
		};	
		Thread updatechat = new Thread(task);
		updatechat.start();
		updatechat.setPriority(Thread.MAX_PRIORITY);

	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 8000);
		Socket messageSocket = new Socket("127.0.0.1",8000);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		InputStreamReader infoStreamReader = new InputStreamReader(messageSocket.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		infoReader = new BufferedReader(infoStreamReader);
		infoWriter = new PrintWriter(messageSocket.getOutputStream());
		System.out.println("networking established");
	
		
		InetAddress inetAddress = sock.getInetAddress();
		System.out.println(path);

		System.out.println();
		
		
	}
	public class UserListTask extends Observable implements Runnable {
		private String message;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(MultiThreadServer.ovUser.hasChanged()){
				System.out.println("changed");
				MultiThreadServer.ovUser.notifyObservers();
			};
		}
	}

	/*class IncomingReader implements Runnable {
		public void run() {
			System.out.print("running");
			String message;
			try {
				message = reader.readLine();
				chatText += (message + "\n");
				System.out.println(chatText);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}*/



}
