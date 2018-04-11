package de.BentiGorlich.BatrikaClient;

import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

public class loadDB extends Task<Void>{
	Main m;
	DataBase db;
	
	loadDB(DataBase db, Main m){
		this.db = db;
		this.m = m;
	}
	
	@Override
	protected Void call() throws Exception {
		Main.standard = new Image(Paths.get("res", "pictures", "standard_profile_picture.png").toUri().toURL().toString(), 2000.0, 2000.0, true, true);
		if(Files.notExists(db.ServerDirectory.toPath())) {
			db.setupServers();
		}
		db.readServers();
		return null;
	}

}
