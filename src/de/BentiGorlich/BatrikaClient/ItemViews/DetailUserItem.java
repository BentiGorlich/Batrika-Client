package de.BentiGorlich.BatrikaClient.ItemViews;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import de.BentiGorlich.BatrikaBasic.ImageButton;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import de.BentiGorlich.BatrikaClient.Basic.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class DetailUserItem extends AnchorPane{
	
	Server server;
	User user;
	ImageButton imgb_back;
	ImageButton imgb_edit;
	ImageButton imgb_save;
	ImageButton imgb_cancel;
	ImageButton imgb_delete_nickname;
	Parent p;
	
	FlowPane button_top = new FlowPane();
	GridPane top = new GridPane();
	
	Node backTo = null;
	
	@FXML
	BorderPane main;
	@FXML
	ImageView profile_picture;
	@FXML
	Label username;
	@FXML
	Label messages_sent;
	@FXML
	Label messages_received;
	@FXML
	Label nickname_label;
	@FXML
	GridPane nickname;
	@FXML
	Label oldnames;
	@FXML
	TextField nickname_text;
	@FXML
	FlowPane buttons;
	
	public DetailUserItem(User user, Server server){
		try {
			Image normal = new Image(Paths.get("res", "pictures", "Buttons", "back_white", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			Image MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "back_white", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			Image clicked = new Image(Paths.get("res", "pictures", "Buttons", "back_white", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			imgb_back = new ImageButton(normal, MouseOver, clicked, this::back, 50.0, 50.0);
			
			normal = new Image(Paths.get("res", "pictures", "Buttons", "edit", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "edit", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "edit", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			imgb_edit = new ImageButton(normal, MouseOver, clicked, this::edit_nickname, 25.0, 25.0);
			
			normal = new Image(Paths.get("res", "pictures", "Buttons", "delete", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "delete", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "delete", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			imgb_delete_nickname = new ImageButton(normal, MouseOver, clicked, user::delete_nickname, 25.0, 25.0);

			normal = new Image(Paths.get("res", "pictures", "Buttons", "delete_white", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "delete_white", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "delete_white", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			ImageButton imgb_delete = new ImageButton(normal, MouseOver, clicked, user::delete, 50.0, 50.0);
			imgb_delete.setSmooth(true);

			normal = new Image(Paths.get("res", "pictures", "Buttons", "shield_white", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "shield_white", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "shield_white", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			ImageButton imgb_blocked = new ImageButton(normal, MouseOver, clicked, user::block, 50.0, 50.0);
			imgb_blocked.setSmooth(true);

			normal = new Image(Paths.get("res", "pictures", "Buttons", "remove_contact_white", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "remove_contact_white", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "remove_contact_white", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			ImageButton imgb_remove = new ImageButton(normal, MouseOver, clicked, user::remove_contact, 50.0, 50.0);
			imgb_remove.setSmooth(true);
			
			button_top.setAlignment(Pos.CENTER_RIGHT);
			button_top.getChildren().addAll(imgb_remove, imgb_blocked, imgb_delete);
			
			top.addRow(0);
			top.addColumn(0);
			top.addColumn(1);
			top.getRowConstraints().add(0, new RowConstraints(50.0));
			top.getColumnConstraints().add(0, new ColumnConstraints(50,50,50));
			top.getColumnConstraints().add(1, new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.RIGHT, true));
			
			top.add(imgb_back, 0, 0);
			top.add(button_top, 1, 0);
			
			AnchorPane.setBottomAnchor(this, 0.0);
			AnchorPane.setTopAnchor(this, 0.0);
			AnchorPane.setLeftAnchor(this, 0.0);
			AnchorPane.setRightAnchor(this, 0.0);
			
			FXMLLoader loader = new FXMLLoader(Paths.get("res", "layouts", "detailUserItem.fxml").toUri().toURL());
			loader.setController(this);
			p = loader.load();
			DetailUserItem dui = loader.getController();
			dui.set(user, server);
			getChildren().add(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void set(User user, Server server) {
		this.user = user;
		this.server = server;
		username.setText(user.getDisplayName());
		profile_picture.fitHeightProperty().bind(main.widthProperty().divide(2.0));
		profile_picture.fitWidthProperty().bind(main.widthProperty().divide(2.0));
		profile_picture.setImage(user.profile_picture);
		main.setTop(top);
		AnchorPane.setBottomAnchor(main, 0.0);
		AnchorPane.setTopAnchor(main, 0.0);
		AnchorPane.setLeftAnchor(main, 0.0);
		AnchorPane.setRightAnchor(main, 0.0);

		buttons.getChildren().add(imgb_edit);
		buttons.getChildren().add(imgb_delete_nickname);
		
		messages_received.textProperty().bind(user.messagesReceived.asString());
		messages_sent.textProperty().bind(user.messagesSent.asString());
		nickname_label.textProperty().bind(user.nickname);
		
		for(int i = 0; i<user.oldnames.size(); i++) {
			if(i>0) {
				oldnames.setText(oldnames.getText() + ", " + user.oldnames.get(i));
			}else {
				oldnames.setText(user.oldnames.get(i));
			}
		}
	}
	
	public void setBackTo(Node backTo) {
		this.backTo = backTo;
	}
	
	private void back(ActionEvent e) {
		Main.static_content.getChildren().clear();
		if(backTo != null) {
			Main.static_content.getChildren().add(backTo);
			backTo = null;
		}else {
			Main.db.divider_pos = Main.static_main.getDividerPositions()[0];
			Main.static_main.setDividerPositions(1.0);
		}
	}
	
	private void edit_nickname(ActionEvent e){
		try {
			nickname_label.setVisible(false);
			nickname_text.setVisible(true);
			Image normal = new Image(Paths.get("res", "pictures", "Buttons", "check", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			Image MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "check", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			Image clicked = new Image(Paths.get("res", "pictures", "Buttons", "check", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			imgb_save = new ImageButton(normal, MouseOver, clicked, this::save_nickname, 25.0, 25.0);
			
			normal = new Image(Paths.get("res", "pictures", "Buttons", "close", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "close", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "close", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			imgb_cancel = new ImageButton(normal, MouseOver, clicked, this::cancel_nickname, 25.0, 25.0);
			
			buttons.getChildren().clear();
			buttons.getChildren().add(imgb_save);
			buttons.getChildren().add(imgb_cancel);
		} catch(MalformedURLException e1) {}
	}
	
	@FXML
	private void save_nickname(ActionEvent e) {
		if(!nickname_text.getText().equals("")) {
			user.nickname.set(nickname_text.getText());
			cancel_nickname(e);
		}
	}
	
	private void cancel_nickname(ActionEvent e) {
		nickname_text.setVisible(false);
		nickname_label.setVisible(true);
		buttons.getChildren().clear();
		buttons.getChildren().add(imgb_edit);
		buttons.getChildren().add(imgb_delete_nickname);
	}
}
