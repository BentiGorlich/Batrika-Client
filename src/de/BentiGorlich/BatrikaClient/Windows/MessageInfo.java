package de.BentiGorlich.BatrikaClient.Windows;

import java.io.IOException;
import java.nio.file.Paths;

import de.BentiGorlich.BatrikaBasic.Helper;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.TextMessage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MessageInfo{
	@FXML
	Label message;
	@FXML
	Label written;
	@FXML
	Label uploaded;
	@FXML
	Label received;
	@FXML
	Label messageID;
	@FXML
	Label seen;
	@FXML
	Label label1;
	@FXML
	Label label2;
	@FXML
	Label label3;
	@FXML
	Label label4;
	
	Scene sc;
	Stage s;
	
	MessageInfo(TextMessage m){
		try{
			FXMLLoader loader = new FXMLLoader(Paths.get("res", "layouts", "MessageInfo.fxml").toUri().toURL());
			loader.setController(this);
			Parent p = loader.load();
			((MessageInfo)loader.getController()).set(m);
			s = new Stage();
			sc = new Scene(p);
			s.setScene(sc);
			s.initModality(Modality.APPLICATION_MODAL);
			s.initOwner(Main.mainstage);
			s.setTitle("Message Information");
			s.setWidth(400);
			s.setY((Main.mainstage.getY()+Main.mainstage.getHeight())/2-200);
			s.setX((Main.mainstage.getX()+Main.mainstage.getWidth())/2+400);
			s.show();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void set(TextMessage m) {
		message.setText(m.message);
		messageID.setText(String.valueOf(m.myMessageID));
		if(m.time_sent != null) {
			written.setText(Helper.getTime(m.time_sent));
		}else {
			written.setVisible(false);
			label1.setVisible(false);
		}
		if(m.time_uploadedToServer != null) {
			uploaded.setText(Helper.getTime(m.time_uploadedToServer));
		}else {
			uploaded.setVisible(false);
			label2.setVisible(false);
		}
		if(m.time_receivedByUser.size() > 0) {
			received.setText(Helper.getTime(m.time_receivedByUser.get(0).time));
		}else {
			received.setVisible(false);
			label3.setVisible(false);
		}
		if(m.time_seenByUser.size() > 0) {
			seen.setText(Helper.getTime(m.time_seenByUser.get(0).time));
		}else {
			seen.setVisible(false);
			label4.setVisible(false);
		}
	}
}
