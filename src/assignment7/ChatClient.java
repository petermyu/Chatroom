package assignment7;

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
	private String IPAddress;
	
	static String path = new File("Chatroom/src/").getParent();
	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
	static UserListObservable userObservable = new UserListObservable();
	static ListView<String> activeList = new ListView<String>();
	
	public static void main(String[] args) {
		launch(args);
	}
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
		TextField setIP = new TextField();
		Text ip = new Text();
		ip.setText("IP Addr : ");
		user.setText("Username : ");
		pword.setText("Password : ");
		password.setDisable(true);
		
		login.setText("Login");
		register.setText("Register");
	
		
		login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	username = userName.getText();
            	IPAddress = setIP.getText();
            //	writer.println(username);
            //	writer.flush();

            	chatSelectPane(primaryStage);
            }
        });
		
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(userName, 2, 0);
//		grid.add(password, 2, 3);
		grid.add(user, 1, 0);
//		grid.add(pword, 1, 3);
		grid.add(login, 2, 4);
		grid.add(register, 3, 4);
		grid.add(ip, 1, 3);
		grid.add(setIP, 2, 3);
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
    		//	UserListWriter newWriter = new UserListWriter(ChatClient.path, "selected_list");
    		//	newWriter.addArrayList(findList);

            	try {
        			setUpNetworking(new ArrayList<String>(selectedList));
        			TimeUnit.MILLISECONDS.sleep(200);
        			GroupChat newChat = new GroupChat(new ArrayList<String>(selectedList), writer, reader, username, infoWriter,infoReader);
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
	

	private void setUpNetworking(ArrayList<String> selectedList) throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket(IPAddress, 8000);
		Socket messageSocket = new Socket(IPAddress,8000);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		InputStreamReader infoStreamReader = new InputStreamReader(messageSocket.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		infoReader = new BufferedReader(infoStreamReader);
		infoWriter = new PrintWriter(messageSocket.getOutputStream());
		System.out.println("networking established");
	
		//send list of selected users
		String concat = "";
		for(String user : selectedList){
			concat.concat(user + " ");
		}
		infoWriter.println(concat);
		infoWriter.flush();
		
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
