package day23network;

import java.util.Observable;

public class ClientObservable extends Observable {
	private String message;
	
	public void setChange(){
		setChanged();
	}
	public void newMessage(String message){
		setChanged();
		notifyObservers(message);
	}
}
