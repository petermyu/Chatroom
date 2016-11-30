package day23network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import day23network.MultiThreadServer.HandleAClient;

public class ServerTask implements Runnable {

	private Socket socket;
	private InetAddress inetAddress;
	public Socket getSocket(){
		return socket;
	}
	public String getAddress(){
		return inetAddress.getHostAddress();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {  // Create a server socket 
			ServerSocket serverSocket = new ServerSocket(8000);
			MultiThreadServer.clientOutputStreams = new ArrayList<PrintWriter>();
			
			while (true) { 
				// Listen for a new connection request 
				socket = serverSocket.accept(); 
				//PrintWriter writer = new PrintWriter(socket.getOutputStream());
				ClientObserver writer = new ClientObserver(socket.getOutputStream());
				inetAddress = socket.getInetAddress();
				MultiThreadServer.clientOutputStreams.add(writer);
			//	userList.put(inetAddress.getHostName(), writer);
				MultiThreadServer.ov.addObserver(writer);
				// Create and start a new thread for the connection
				
			} 
		} 
		catch(IOException ex) { 
			System.err.println(ex);
		}
	}

}
