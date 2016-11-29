package day23network;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver extends PrintWriter implements Observer {
	private String message;
	public ClientObserver(OutputStream out) {
		super(out);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		System.out.println("updated");
		this.println(arg);
		this.flush();
	}
	
	
}
