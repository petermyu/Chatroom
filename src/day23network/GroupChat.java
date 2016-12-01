package day23network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GroupChat extends Application{
	
	private ArrayList<String> selectedList;
	private PrintWriter writer;
	private BufferedReader reader;
	private String username;
	private PrintWriter infoWriter;
	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
	public GroupChat(ArrayList<String> list, PrintWriter writer, BufferedReader reader, String username, PrintWriter infoWriter){
		selectedList = list;
		this.writer = writer;
		this.reader = reader;
		this.username = username; 
		this.infoWriter = infoWriter;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
			Stage newStage =primaryStage;
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			TextArea chatField = new TextArea();
			TextField inputMessage = new TextField();

			UserListWriter cWriter = new UserListWriter(ChatClient.path, "client_info");
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
							UserListWriter cWriter = new UserListWriter(ChatClient.path, "client_info");
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
	}
}
