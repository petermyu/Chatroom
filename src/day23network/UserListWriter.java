package day23network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserListWriter {
	private static int clientNo;
	private String path;
	private File file;
	public UserListWriter(String path){
		this.path = path;
		file = new File(path+ "whitelist.txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int getClientNo(){
		return clientNo;
	}
	public boolean userExists(String username){
			try {
				String entry;
				BufferedReader reader = new BufferedReader(new FileReader(file));
				while((entry = reader.readLine()) !=null){
					if(entry == username){
						return true;
					}
				}
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		
		
	}
	public void addUser(String username){
		try {
			
			String content = username;
			File file = new File(path+ "whitelist.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			if(userExists(username) || username == null){
				return;
			}
			clientNo++;
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content+"\n");
		
			bw.close();
			System.out.println("added :" + username + " to file at " + path);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<String> readUsers(){
		String sCurrentLine;
		ArrayList<String> userList = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((sCurrentLine = br.readLine()) != null) {
				userList.add(sCurrentLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userList;
		
	}
}
