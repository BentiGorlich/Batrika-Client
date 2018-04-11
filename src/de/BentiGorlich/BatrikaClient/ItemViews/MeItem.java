package de.BentiGorlich.BatrikaClient.ItemViews;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaBasic.Helper;
import de.BentiGorlich.BatrikaBasic.ImageButton;
import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaBasic.Visibility;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.PictureButton;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import de.BentiGorlich.BatrikaClient.Network.SendMedia;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MeItem extends AnchorPane{
	
	Server server;
	
	Parent p;
	@FXML
	GridPane top;
	@FXML
	FlowPane buttons;
	@FXML
	Label accountname;
	@FXML
	GridPane email;
	@FXML
	GridPane displayname;
	@FXML
	ChoiceBox<String> visibility;
	@FXML
	ImageView imv;
	
	Label email_l;
	Label displayname_l;
	
	TextField email_text = new TextField();
	TextField displayname_text = new TextField();
	
	ImageButton imgb_email_edit;
	ImageButton imgb_email_save;
	ImageButton imgb_email_cancel;
	
	ImageButton imgb_displayname_edit;
	ImageButton imgb_displayname_save;
	ImageButton imgb_displayname_cancel;
	
	ImageButton imgb_save;
	ImageButton imgb_cancel;
	
	boolean isEditEmail = false;
	boolean isEditDisplayname = false;
	
	String checksum = "";
	File path = null;
	
	public MeItem(Server server) {
		this.server = server;
		
		AnchorPane.setBottomAnchor(this, 0.0);
		AnchorPane.setTopAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 0.0);
		AnchorPane.setRightAnchor(this, 0.0);
		
		try {
			FXMLLoader loader = new FXMLLoader(Paths.get("res", "layouts", "MeItem.fxml").toUri().toURL());
			loader.setController(this);
			p = loader.load();

			AnchorPane.setBottomAnchor(p, 0.0);
			AnchorPane.setTopAnchor(p, 0.0);
			AnchorPane.setLeftAnchor(p, 0.0);
			AnchorPane.setRightAnchor(p, 0.0);
			
			getChildren().add(p);
			MeItem i = loader.getController();
			
			Image normal = new Image(Paths.get("res", "pictures", "Buttons", "edit", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			Image MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "edit", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			Image clicked = new Image(Paths.get("res", "pictures", "Buttons", "edit", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			
			imgb_email_edit = new ImageButton(normal, MouseOver, clicked, this::editEmail, 25, 25);
			imgb_displayname_edit = new ImageButton(normal, MouseOver, clicked, this::editDisplayname, 25, 25);
			
			normal = new Image(Paths.get("res", "pictures", "Buttons", "check", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "check", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "check", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			
			imgb_email_save = new ImageButton(normal, MouseOver, clicked, this::saveEmail, 25, 25);
			imgb_displayname_save = new ImageButton(normal, MouseOver, clicked, this::saveDisplayname, 25, 25);
			
			normal = new Image(Paths.get("res", "pictures", "Buttons", "close", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "close", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "close", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			
			imgb_email_cancel = new ImageButton(normal, MouseOver, clicked, this::cancelEmail, 25, 25);
			imgb_displayname_cancel = new ImageButton(normal, MouseOver, clicked, this::cancelDisplayname, 25, 25);

			normal = new Image(Paths.get("res", "pictures", "Buttons", "close_white", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "close_white", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "close_white", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			imgb_cancel = new ImageButton(normal, MouseOver, clicked, this::cancel, 50, 50);
			imgb_cancel.setToolTip("cancel");
			
			normal = new Image(Paths.get("res", "pictures", "Buttons", "check_white", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "check_white", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "check_white", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			imgb_save = new ImageButton(normal, MouseOver, clicked, this::save, 50, 50);
			imgb_save.setToolTip("save properties");
			i.setStuff(server);

			GridPane.setMargin(email_l, new Insets(10));
			GridPane.setMargin(email_text, new Insets(10));
			GridPane.setMargin(displayname_l, new Insets(10));
			GridPane.setMargin(displayname_text, new Insets(10));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void editEmail(ActionEvent e) {
		email.getChildren().clear();
		email.add(email_text, 0, 0);
		email.add(imgb_email_save, 1, 0);
		email.add(imgb_email_cancel, 2, 0);
		email_text.setText(email_l.getText());
		isEditEmail = true;
	}
	
	private void saveEmail(ActionEvent e) {
		if(isEditEmail) {
			email_l.setText(email_text.getText());
			cancelEmail(e);
		}
	}
	
	private void cancelEmail(ActionEvent e) {
		email.getChildren().clear();
		email.add(email_l, 0, 0);
		email.add(imgb_email_edit, 2, 0);
		isEditEmail = false;
	}
	
	private void editDisplayname(ActionEvent e) {
		displayname.getChildren().clear();
		displayname.add(displayname_text, 0, 0);
		displayname.add(imgb_displayname_save, 1, 0);
		displayname.add(imgb_displayname_cancel, 2, 0);
		displayname_text.setText(displayname_l.getText());
		isEditDisplayname = true;
	}
	
	private void saveDisplayname(ActionEvent e) {
		if(isEditDisplayname) {
			displayname_l.setText(displayname_text.getText());
			cancelDisplayname(e);
		}
	}
	
	private void cancelDisplayname(ActionEvent e) {
		displayname.getChildren().clear();
		displayname.add(displayname_l, 0, 0);
		displayname.add(imgb_displayname_edit, 2, 0);
		isEditDisplayname = false;
	}
	
	private void save(ActionEvent e) {
		String checksum = "";
		if(path != null) {
			try {
				checksum = Helper.getFileChecksum(MessageDigest.getInstance("SHA-1"), path);
			} catch (NoSuchAlgorithmException | IOException e2) {
				e2.printStackTrace();
			}
		}
		String selected = visibility.getSelectionModel().getSelectedItem();
		Visibility vis = null;
		if(!selected.contains("current")) {
			switch(visibility.getSelectionModel().getSelectedItem()) {
			case "Everyone":
				vis = Visibility.everyone;
				break;
			case "Friends":
				vis = Visibility.Friends;
				break;
			case "Friends-of-Friends":
				vis = Visibility.FriendsOfFriends;
				break;
			}
		}
		saveDisplayname(e);
		saveEmail(e);
		if(
			!server.email.equals(email_l.getText())
			|| !server.getDisplayname().equals(displayname_l.getText())
			|| vis != null
			|| !checksum.equals(this.checksum)
		) {
			JSONObject m = new JSONObject();
			try {
				m
					.put("type", MessageType.user_change.toInt())
					.put("displayname", displayname_l.getText())
					.put("email", email_l.getText())
					.put("userID", server.userID)
				;
				if(vis != null){
					m.put("visibility", vis.toInt());
				}
				server.send(m);
				if(!checksum.equals(this.checksum) && path != null) {
					JSONObject message = new JSONObject();
					message
						.put("type", MessageType.profile_picture.toInt())
						.put("initialize", true)
						.put("total_size", path.length())
						.put("name", Calendar.getInstance().getTimeInMillis())
						.put("request", false)
					;
					SendMedia pp = new SendMedia(server, message, path, server.userID);
					Thread t = new Thread(pp);
					t.start();
				}
			} catch(JSONException e2) {}
		}
	}
	private void choose_pic(ActionEvent e) {
		try {
			FileChooser pic_file = new FileChooser();
			pic_file.setTitle("Select the ProfilePicture for your server");
			ExtensionFilter pictures = new ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.bmp");
			pic_file.getExtensionFilters().add(pictures);
			File path = pic_file.showOpenDialog(Main.mainstage);
			if(path != null) {
				this.path = path;
				Image profile = new Image(path.toURI().toURL().toString(), 2000.0, 2000.0, true, true);
				imv.setImage(profile);
			}
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		}
	}
	private void cancel(ActionEvent e) {
		
	}

	private void setStuff(Server server) {
		accountname.setText(server.getAccountName());
		switch(server.visiblility) {
		case everyone:
			visibility.getItems().addAll("current: Everyone", "Friends", "Friends-of-Friends");
			break;
		case Friends:
			visibility.getItems().addAll("current: Friends", "Everyone", "Friends-of-Friends");
			break;
		case FriendsOfFriends:
			visibility.getItems().addAll("current: Friends-of-Friends", "Everyone", "Friends");
			break;
		}
		visibility.getSelectionModel().selectFirst();
		buttons.getChildren().addAll(imgb_save, imgb_cancel);
		email.add(imgb_email_edit, 2, 0);
		email_l = new Label(server.email);
		email.add(email_l, 0, 0);
		displayname.add(imgb_displayname_edit, 2, 0);
		displayname_l = new Label(server.getDisplayname());
		displayname.add(displayname_l, 0, 0);
		
		new PictureButton(imv, this::choose_pic);
		imv.setImage(server.profile_picture);
		imv.fitWidthProperty().bind(this.widthProperty().divide(2.0));
		imv.fitHeightProperty().bind(this.widthProperty().divide(2.0));
		
		File pp = new File(Main.db.ServerDirectory + "\\" + server.Servername + "\\profile.png");
		try {
			checksum = Helper.getFileChecksum(MessageDigest.getInstance("SHA-1"), pp);
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}

}
