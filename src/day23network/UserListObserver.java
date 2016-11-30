package day23network;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javafx.collections.FXCollections;
public class UserListObserver implements Observer{
	private String path;
	public UserListObserver(String path){
		this.path = path;
	}
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		UserListWriter newList = new UserListWriter(path);
		ArrayList<String> userList = newList.readUsers();
		System.out.println(userList);
		ChatClient.activeList.setItems(FXCollections.observableArrayList(userList));
		System.out.println("updated list");
		
	}


}
