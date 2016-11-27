package day23network;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
	private String chatText;
	
	public String getText(){
		return chatText;
	}
	public void setText(String text){
		chatText = text;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		initView(primaryStage);
	//	setUpNetworking();
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
		login.setText("Login");
		register.setText("Register");
		
		login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               setChatPane(primaryStage);
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
	}
	private void setChatPane(Stage primaryStage){
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		TextField inputMessage = new TextField();
		final TextField chatField = new TextField();
		chatField.setText("chat started \n aefa");
		chatField.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		
		chatField.setDisable(true);
		Text user = new Text();
		Text pword = new Text();
		Button send = new Button();
		send.setText("Send");
		grid.add(chatField, 1, 0);
		grid.add(inputMessage, 1, 1);
		grid.add(send, 2, 1);
		Scene scene = new Scene(grid,800,400);
		primaryStage.setScene(scene);
		primaryStage.show();
		try{
		setUpNetworking();
		}
		catch(Exception e){
			System.out.println(e);
		}
	/*
		Platform.runLater(new Runnable(){
			@Override
			public void run(){
			String response;
			try {
				response = reader.readLine();
				chatField.appendText(response + "\n");
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
		});*/
		Task task = new Task<Void>() {
		    @Override public Void call() {
		    	String response;
				try {
					while ((response = reader.readLine()) != null) {
					chatField.appendText(response + "\n");
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
		send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	writer.println(inputMessage.getText());
        		writer.flush();
        		inputMessage.clear();
            }
        });
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 8000);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}

	/*class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			writer.println(outgoing.getText());
			writer.flush();
			outgoing.setText("");
			outgoing.requestFocus();
		}
	}
*/

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					chatText += (message + "\n");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	class ChatUpdater implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}}


}
