package de.BentiGorlich.BatrikaClient.Network;

import java.net.Socket;

import javafx.concurrent.Task;

public class Connect extends Task<Socket>{

	String host;
	Integer port;
	
	public Connect(String host, Integer port) {
		this.host = host;
		this.port = port;
	}
	
	@Override
	protected Socket call() throws Exception {
		Socket erg = new Socket(host, port);
		return erg;
	}

}
