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
import javafx.scene.input.*;
import javafx.scene.input.KeyEvent;
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
	private static String chatText = "";
	private TextArea chatField = new TextArea();
	public String getText(){
		return chatText;
	}
	public void setText(String text){
		chatText = text;
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
		try {
			setUpNetworking();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void setChatPane(Stage primaryStage){
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		TextField inputMessage = new TextField();
		
		chatField.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		chatField.setText("");
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
	
/*		Platform.runLater(new Runnable(){
			@Override
			public void run(){
			String response;
			try {
				response = reader.readLine();
				chatField.setText(chatText);
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
		}); */
		
		
		send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(inputMessage.getText() != null){
	            	writer.println(inputMessage.getText());
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
		        	writer.println(inputMessage.getText());
	        		writer.flush();
	        		inputMessage.clear();
		        	}
	        	}
	        }
	    });
		Task task = new Task<Void>() {
		    @Override public Void call() {
		    	String response;
				try {
					System.out.print("run");
					response = reader.readLine();
					while ((response = reader.readLine()) != null) {
						System.out.println("received " + response);
						chatField.appendText(response + "\n");
					}
					/*if(inputMessage.getText() == null){
						send.setDisable(true);
					}
					else{
						send.setDisable(false);
					}*/
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
		updatechat.setPriority(Thread.MAX_PRIORITY-1);

	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 8000);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
	//	Thread readerThread = new Thread(new IncomingReader());
	//	readerThread.start();
	//	readerThread.setPriority(Thread.MAX_PRIORITY);
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
	}


}
