package de.BentiGorlich.BatrikaClient.Network;

import java.io.DataInputStream;
import org.json.JSONObject;

import javafx.concurrent.Task;

public class InputStreamListener extends Task<JSONObject>{

	boolean running = true;
	DataInputStream in;
	
	public InputStreamListener(DataInputStream is) {
		in = is;
	}
	
	public void stop() {
		this.cancel(true);
	}

	@Override
	protected JSONObject call() throws Exception{
		if(in == null || !running) {
			throw new Exception();
		}
		System.out.println("running...");
		running();
		String m = in.readUTF();
		//System.out.println("received: " + m);
		JSONObject message = new JSONObject(m);
		updateMessage(message.toString());
		
		return null;
	}	
}