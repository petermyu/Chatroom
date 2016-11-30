package day23network;

import java.util.Observable;

public class UserListObservable extends Observable{
	public void setChange(){
		setChanged();
	}
}
