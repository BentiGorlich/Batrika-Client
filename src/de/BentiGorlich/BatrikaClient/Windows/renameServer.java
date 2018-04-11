package de.BentiGorlich.BatrikaClient.Windows;

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

public class renameServer {
	
	Server server;
	Stage s;
	Scene sc;
	
	TextField servername;
	
	public renameServer(Server server) {
		this.server = server;
		servername = new TextField();
		servername.setText(server.Servername);
		servername.setOnAction(this::changeName);
		GridPane.setMargin(servername, new Insets(10));
		GridPane.setColumnSpan(servername, 2);
		
		Button change = new Button("change serverame");
		change.setOnAction(this::changeName);
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
		
		main.add(servername, 0, 0);
		main.add(change, 1, 1);
		
		VBox content = new VBox();
		content.setAlignment(Pos.CENTER);
		content.getChildren().add(main);
		
		s = new Stage();
		sc = new Scene(content);
		s.initModality(Modality.APPLICATION_MODAL);
		s.initOwner(Main.mainstage);
		s.setTitle("Rename server");
		s.setScene(sc);
		s.setHeight(100);
		s.setWidth(400);
		s.setY((Main.mainstage.getY()+Main.mainstage.getHeight())/2-200);
		s.setX((Main.mainstage.getX()+Main.mainstage.getWidth())/2-100);
		s.show();
	}
	
	private void changeName(ActionEvent e) {
		if(server.renameServer(servername.getText())) {
			s.close();
		}
	}
}
