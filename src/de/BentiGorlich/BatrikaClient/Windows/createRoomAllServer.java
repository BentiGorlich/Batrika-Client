package de.BentiGorlich.BatrikaClient.Windows;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaClient.Main;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;

public class createRoomAllServer extends createRoom{
	ChoiceBox<String> cb = new ChoiceBox<String>();
	public createRoomAllServer() {
		super("");
		for(int i = 0; i<Main.db.servers.size(); i++) {
			cb.getItems().add(Main.db.servers.get(i).Servername);
		}
		cb.getSelectionModel().selectFirst();
		announce.setText("Create Room");
		GridPane.setMargin(cb, new Insets(10));
		
		main.addRow(6);
		main.add(cb, 0, 6);
	}
	
	@Override
	protected void save(ActionEvent e) {
		String servername = cb.getSelectionModel().getSelectedItem();
		s = Main.db.getServer(servername);
		String roomname = rname.getText();
		boolean hasPW = false;
		String pw = this.pw.getText();
		if(pw.length()>0) {
			hasPW = true;
		}
		if(roomname.length()>0 && s != null) {
			JSONObject m = new JSONObject();
			try {
				m
					.put("type", MessageType.room_create.toInt())
					.put("roomname", roomname)
					.put("hasPW", hasPW)
					.put("hasWL", false)
					.put("hasBL", false)
					.put("timestamp", Calendar.getInstance().getTimeInMillis())
					.put("password", pw)
				;
				s.send(m);
				back(e);
			}catch(JSONException e2) {
				e2.printStackTrace();
			}
		}
	}
}
