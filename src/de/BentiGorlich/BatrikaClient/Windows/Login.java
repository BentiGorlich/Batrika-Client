package de.BentiGorlich.BatrikaClient.Windows;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class Login {

	@FXML
	TextField username;
	@FXML
	PasswordField pw1_p;
	@FXML
	TextField pw1;
	@FXML
	PasswordField pw2_p;
	@FXML
	TextField pw2;
	@FXML
	CheckBox cb_show1;
	@FXML
	CheckBox cb_show2;
	@FXML
	CheckBox cb_nuser;
	@FXML
	GridPane main;
	@FXML
	Button goBTN;
	
	ImageView imv = new ImageView();
	Label accountname = new Label("Account Name");
	Label email = new Label("E-Mail Address");
	TextField accountname_text = new TextField();
	TextField email_text = new TextField();
	Button choose_picture = new Button("Choose Picture");
	
	Scene s;
	Parent p;
	Stage st;
	String servername;
	Server server;
	public Login() {	}

	public void build(Server curr_server, String message) {
		// TODO Auto-generated method stub
		try {
			FXMLLoader loader = new FXMLLoader(Paths.get("res", "layouts", "server login.fxml").toUri().toURL());
			loader.setController(this);
			p = loader.load();
			s = new Scene(p);
			st = new Stage();
			st.initOwner(Main.mainstage);
			st.initModality(Modality.APPLICATION_MODAL);
			st.setScene(s);
			Login temp = loader.getController();
			temp.setServer(curr_server, message);
			
			pw1_p.visibleProperty().bind(cb_show1.selectedProperty().not());
			pw1.visibleProperty().bind(cb_show1.selectedProperty());
			pw2_p.visibleProperty().bind(cb_show2.selectedProperty().not());
			pw2.visibleProperty().bind(cb_show2.selectedProperty());
			
			st.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setServer(Server s, String message) {
		server = s;
		servername = s.Servername;
		st.setTitle("Login to " + s.Servername + ", " + message);
	}
	
	public void save(ActionEvent e) {
		String pw1, pw2;
		if(this.pw1.isVisible()) {
			pw1 = this.pw1.getText();
		}else {
			pw1 = this.pw1_p.getText();
		}
		if(this.pw2.isVisible()) {
			pw2 = this.pw2.getText();
		}else {
			pw2 = this.pw2_p.getText();
		}
		if(!cb_nuser.isSelected()) {
			if(!username.getText().equals("") && pw1.equals(pw2)) {
				for(int i = 0; i<Main.db.servers.size(); i++) {
					if(Main.db.servers.get(i).Servername.equals(servername)) {
						Server server = Main.db.servers.get(i);
						server.setAccountname(username.getText());
						server.userpassword = pw1;
						server.canLogin = true;
						server.connect();
						cancel(e);
					}
				}
			}
		}else {
			if(!username.getText().equals("") 
					&& !accountname_text.getText().equals("")
					&& pw1.equals(pw2)
					&& !email_text.getText().equals("")
			) {
				if(!imv.getImage().equals(Main.standard)) {
					//TODO pp sending
				}
				server.connect();
				server.createUser(email_text.getText(), pw1, accountname_text.getText(), username.getText());
				for(int i = 0; i<Main.db.servers.size(); i++) {
					if(Main.db.servers.get(i).Servername.equals(servername)) {
						Server server = Main.db.servers.get(i);
						server.setAccountname(accountname_text.getText());
						server.userpassword = pw1;
						server.canLogin = true;
						server.email = email_text.getText();
						server.setDisplayname(username.getText());
						server.profile_picture = imv.getImage();
						server.connect();
						server.login();
					}
				}
				cancel(e);
			}
		}
	}
	
	public void cancel(ActionEvent e) {
		((Stage)((Node)e.getSource()).getScene().getWindow()).close();
	}
	
	public void show1(ActionEvent e){
		if(cb_show1.isSelected()) {
			pw1.setText(pw1_p.getText());
		}else {
			pw1_p.setText(pw1.getText());
		}
	}
	
	public void show2(ActionEvent e) {
		if(cb_show2.isSelected()) {
			pw2.setText(pw2_p.getText());
		}else {
			pw2_p.setText(pw2.getText());
		}
	}
	
	public void new_user(ActionEvent e) throws MalformedURLException {
		if(cb_nuser.isSelected()) {
			goBTN.setText("Create User");
			imv.setImage(Main.standard);
			imv.setFitHeight(300.0);
			imv.setFitWidth(300.0);
			imv.setPreserveRatio(true);
			
			choose_picture.setOnAction(this::choose_picture);
			
			GridPane.setColumnSpan(accountname_text, 2);
			GridPane.setColumnSpan(email_text, 2);
			GridPane.setColumnSpan(imv, 2);
			
			main.add(accountname, 0, 0);
			main.add(accountname_text, 1, 0);
			main.add(email, 0, 4);
			main.add(email_text, 1, 4);
			main.add(imv, 0, 5);
			main.add(choose_picture, 2, 5);
			
		}else {
			main.getChildren().remove(accountname);
			main.getChildren().remove(accountname_text);
			main.getChildren().remove(email);
			main.getChildren().remove(email_text);
			main.getChildren().remove(imv);
			main.getChildren().remove(choose_picture);
		}
	}
	
	private void choose_picture(ActionEvent e) {
		FileChooser pic_file = new FileChooser();
		pic_file.setTitle("Select the ProfilePicture for your server");
		ExtensionFilter pictures = new ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.bmp");
		pic_file.getExtensionFilters().add(pictures);
		File path = pic_file.showOpenDialog(st);
		Image profile;
		try {
			profile = new Image(path.toURI().toURL().toString(), 2000.0, 2000.0, true, true);
			imv.setImage(profile);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}
}
