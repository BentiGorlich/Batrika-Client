package de.BentiGorlich.BatrikaClient.Windows;

import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RoomInsertPW {

	Server server;
	Stage s;
	Scene sc;
	
	TextField pw;
	String roomname;
	
	public RoomInsertPW(Server server, String roomname){
		this.server = server;
		this.roomname = roomname;
		pw = new TextField();
		pw.setText(server.Servername);
		pw.setOnAction(this::savePW);
		GridPane.setMargin(pw, new Insets(10));
		GridPane.setColumnSpan(pw, 2);
		
		Button change = new Button("Insert Password for Room " + roomname);
		change.setOnAction(this::savePW);
		GridPane.setMargin(change, new Insets(10));

		GridPane main = new GridPane();
		main.addRow(0);
		main.getRowConstraints().add(0, new RowConstraints(30, 30, 30, Priority.NEVER, VPos.CENTER, true));
		main.addColumn(0);
		main.getColumnConstraints().add(0, new ColumnConstraints(-1,-1,-1,Priority.ALWAYS, HPos.CENTER, true));
		main.addColumn(1);
		main.getColumnConstraints().add(new ColumnConstraints(10, 200, 200, Priority.NEVER, HPos.LEFT, true));
		main.addRow(1);
		main.getRowConstraints().add(new RowConstraints(30,30,30,Priority.NEVER, VPos.CENTER, true));
		
		main.add(pw, 0, 0);
		main.add(change, 1, 1);
		
		VBox content = new VBox();
		content.setAlignment(Pos.CENTER);
		content.getChildren().add(main);
		
		s = new Stage();
		sc = new Scene(content);
		s.initModality(Modality.APPLICATION_MODAL);
		s.initOwner(Main.mainstage);
		s.setTitle("Join Room " + roomname);
		s.setScene(sc);
		s.setHeight(100);
		s.setWidth(400);
		s.setY((Main.mainstage.getY()+Main.mainstage.getHeight())/2-200);
		s.setX((Main.mainstage.getX()+Main.mainstage.getWidth())/2-100);
		s.show();
	}
	
	private void savePW(ActionEvent e) {
		if(!pw.getText().equals("")) {
			JSONObject m_join_room = new JSONObject();
			try {
				m_join_room
					.put("type", MessageType.room_join.toInt())
					.put("roomname", roomname)
					.put("password", pw.getText())
				;
				server.send(m_join_room);
				s.close();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}
}
