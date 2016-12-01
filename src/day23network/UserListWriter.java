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
import java.util.Scanner;

public class UserListWriter {
	private static int clientNo;
	private String path;
	private File file;
	public UserListWriter(String path, String name){
		this.path = path;
		
		file = new File(path+ name+ ".txt");
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
					if(entry.equals(username)){
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
		if(userExists(username)){
			return;
		}
		try {
			String content = username;
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
	public void updateClientText(String text){
		try {
			String content = text;
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			if(userExists(text) || text == null){
				return;
			}
			clientNo++;
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content+"\n");
		
			bw.close();
			System.out.println("added :" + text + " to file at " + path);

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
	public String readString(){
		try {
			@SuppressWarnings("resource")
			Scanner scan = new Scanner(file);
			System.out.println("has next: " + scan.hasNextLine());
			String r= scan.nextLine();
			return r;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "exception";
		}
		
	}
	public void addArrayList(ArrayList<String> list){
		try{
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for(String user : list){
			bw.write(user+"\n");
		}
		bw.close();

	} catch (IOException e) {
		e.printStackTrace();
	}
	}
}
