package de.BentiGorlich.BatrikaClient.Network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.nio.file.Paths;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CheckConnection extends Task<Boolean>{
	
	ImageView imv;
	ProgressIndicator progress;
	String hostname; 
	int port;
	Image success;
	Image fail;
	
	public CheckConnection(ImageView imv, ProgressIndicator progress, String hostname, int port){
		this.imv = imv;
		this.progress = progress;
		this.hostname = hostname;
		this.port = port;
		try {
			success = new Image(Paths.get("res", "pictures", "success.png").toUri().toURL().toString(), 60.0, 60.0, true, true);
			fail = new Image(Paths.get("res", "pictures", "fail.png").toUri().toURL().toString(), 60.0, 60.0, true, true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		imv.setVisible(false);
		progress.setProgress(-1);
		progress.setVisible(true);
	}
	
	@Override
	public Boolean call() {
		try {
			Socket con = new Socket(hostname, port);
			con.close();
			progress.setVisible(false);
			imv.setImage(success);
			imv.setVisible(true);
			updateValue(true);
			return true;
		} catch(IOException e) {
			progress.setVisible(false);
			imv.setImage(fail);
			imv.setVisible(true);
			updateValue(false);
			return false;
		}
	}
}
