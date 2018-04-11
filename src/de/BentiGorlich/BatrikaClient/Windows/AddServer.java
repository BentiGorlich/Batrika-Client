package de.BentiGorlich.BatrikaClient.Windows;
import java.io.IOException;
import java.nio.file.Paths;

import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import de.BentiGorlich.BatrikaClient.Network.CheckConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddServer {
	
	@FXML
	TextField pw;
	@FXML
	PasswordField pw_p;
	@FXML
	ProgressIndicator progress;
	@FXML
	ImageView imv;
	@FXML
	CheckBox cb_show;
	@FXML
	TextField servername;
	@FXML
	TextField port;
	@FXML
	TextField hostname;

	Scene s;
	Parent p;
	Stage st;
	Server server;
	
	Image success;
	Image fail;
	
	boolean valid = false;
	public AddServer() {
		
	}
	
	public void build() {
		FXMLLoader loader;
		try {
			loader = new FXMLLoader(Paths.get("res", "layouts", "add server.fxml").toUri().toURL());
			loader.setController(this);
			p = loader.load();
			s = new Scene(p);
			st = new Stage();
			imv.setImage(fail);
			imv.setVisible(true);
			progress.setVisible(false);
			st.initOwner(Main.mainstage);
			st.initModality(Modality.APPLICATION_MODAL);
			st.setScene(s);
			st.show();
			hostname.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> val, Boolean arg1, Boolean arg2) {
					if(!val.getValue()) {
						if(!hostname.getText().equals("") && !port.getText().equals("")) {
							try{
								int p = Integer.parseInt(port.getText());
								CheckConnection cc = new CheckConnection(imv, progress, hostname.getText(), p);
								Thread t = new Thread(cc);
								t.setDaemon(true);
								t.start();
								cc.valueProperty().addListener(new ChangeListener<Boolean>() {
									@Override
									public void changed(ObservableValue<? extends Boolean> val, Boolean arg1,Boolean arg2) {
										valid = val.getValue().booleanValue();
									}
								});
							} catch (NumberFormatException e) {	}
						}
					}
				}
			});
			port.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> val, Boolean arg1, Boolean arg2) {
					if(!val.getValue()) {
						if(!hostname.getText().equals("") && !port.getText().equals("")) {
							try{
								int p = Integer.parseInt(port.getText());
								CheckConnection cc = new CheckConnection(imv, progress, hostname.getText(), p);
								Thread t = new Thread(cc);
								t.setDaemon(true);
								t.start();
								cc.valueProperty().addListener(new ChangeListener<Boolean>() {
									@Override
									public void changed(ObservableValue<? extends Boolean> val, Boolean arg1,Boolean arg2) {
										valid = val.getValue().booleanValue();
									}
								});
							} catch (NumberFormatException e) {	}
						}
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void save(ActionEvent e) {
		String pw = pw_p.getText();
		if(cb_show.isSelected()) {
			pw = this.pw.getText();
		}
		if(server == null) {
			if(valid) {
				//if(!Main.db.hasServer(hostname.getText())) {
					server = Main.db.addServer(servername.getText(), hostname.getText(), pw, Integer.parseInt(port.getText()));
					new Login().build(server, "Login to " + server.Servername);
					((Stage)((Node)e.getSource()).getScene().getWindow()).close();
				//}else {
				//	Alert a = new Alert(AlertType.ERROR, "This Server-address is already in your servers", ButtonType.OK);
				//	a.show();
				//}
			}else {
				Alert a = new Alert(AlertType.ERROR, "This is not a valid configuration", ButtonType.OK);
				a.show();
			}
		}else {
			
		}
	}
	
	@FXML
	private void show(ActionEvent e) {
		if(cb_show.isSelected()) {
			pw.setText(pw_p.getText());
			pw.setVisible(true);
			pw_p.setVisible(false);
		}else {
			pw_p.setText(pw.getText());
			pw_p.setVisible(true);
			pw.setVisible(false);
		}
	}
	
	@FXML
	private void cancel(ActionEvent e) {
		((Stage)((Node)e.getSource()).getScene().getWindow()).close();
	}
}
