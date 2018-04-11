package de.BentiGorlich.BatrikaClient.Basic;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaBasic.Helper;
import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.ItemViews.UserItem;
import de.BentiGorlich.BatrikaClient.ItemViews.UserItemAllTab;
import de.BentiGorlich.BatrikaClient.Windows.UserChat;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

public class User {
	public int userID;
	public boolean isContact = false;
	public SimpleIntegerProperty messagesReceived = new SimpleIntegerProperty(0);
	public SimpleIntegerProperty messagesSent = new SimpleIntegerProperty(0);
	public SimpleIntegerProperty unseenMessages = new SimpleIntegerProperty(0);
	public SimpleBooleanProperty isBlocked = new SimpleBooleanProperty(false);
	private String displayName;
	public Image profile_picture;
	public ObservableList<TextMessage> conversation = FXCollections.observableArrayList();
	public ArrayList<String> oldnames = new ArrayList<String>();
	public UserItem ui;
	public UserItemAllTab uiat;
	public UserItem uii;
	public UserItemAllTab uiati;
	public UserChat chat;
	public Server server;
	public SimpleObjectProperty<TextMessage> lastMessage = new SimpleObjectProperty<TextMessage>();
	public SimpleStringProperty nickname = new SimpleStringProperty();
	public Paint color = Helper.randomColor();

	public User(String name, int id) {
		this.userID = id;
		this.displayName = name;
	}
	
	public void addMessage(TextMessage m) {
		if(ui == null) {
			ui = new UserItem(this, false);
			server.serverView.add(ui);
		}
		if(uiat == null) {
			uiat = new UserItemAllTab(this, server, false);
			Main.static_all_chats.getChildren().add(uiat);
		}
		if(m.fromMe) {
			messagesSent.set(messagesSent.get() + 1);
		}else {
			messagesReceived.set(messagesReceived.get() + 1);
		}
		lastMessage.set(m);
		conversation.add(m);
	}

	public void setName(String newName) {
		if(ui != null) {
			ui.setTopText(newName);
		}
		if(uii != null) {
			uii.setTopText(newName);
		}
		if(uiat != null) {
			uiat.setTopText(newName);
		}
		if(uiati != null) {
			uiati.setTopText(newName);
		}
		if(!oldnames.contains(displayName)) {
			oldnames.add(displayName);
		}
		if(chat != null) {
			chat.item.setTopText(newName);
		}
		this.displayName = newName;
	}
	
	public void setLoadPP() {
		if(ui != null) {
			ui.setLoadPP();
		}
		if(uii != null) {
			uii.setLoadPP();
		}
		if(uiat != null) {
			uiat.setLoadPP();
		}
		if(uiati != null) {
			uiati.setLoadPP();
		}
		if(chat != null) {
			chat.item.setLoadPP();
		}
	}
	
	public void setMinMax(double minValue, double maxValue) {
		if(ui != null) {
			ui.setMinMax(minValue, maxValue);
		}
		if(uii != null) {
			uii.setMinMax(minValue, maxValue);
		}
		if(uiat != null) {
			uiat.setMinMax(minValue, maxValue);
		}
		if(uiati != null) {
			uiati.setMinMax(minValue, maxValue);
		}
		if(chat != null) {
			chat.item.setMinMax(minValue, maxValue);
		}
	}
	
	public Double getProgress() {
		return uii.getProgress();
	}
	
	public void setProgress(double progress) {
		if(ui != null) {
			ui.setProgress(progress);
		}
		if(uii != null) {
			uii.setProgress(progress);
		}
		if(uiat != null) {
			uiat.setProgress(progress);
		}
		if(uiati != null) {
			uiati.setProgress(progress);
		}
		if(chat != null) {
			chat.item.setProgress(progress);
		}
	}
	
	public void setProgressCompleted() {
		if(ui != null) {
			ui.setProgressCompleted();
		}
		if(uii != null) {
			uii.setProgressCompleted();
		}
		if(uiat != null) {
			uiat.setProgressCompleted();
		}
		if(uiati != null) {
			uiati.setProgressCompleted();
		}
		if(chat != null) {
			chat.item.setProgressCompleted();
		}
	}

	public void setProfilePicture(Image image) {
		this.profile_picture = image;
		if(ui != null) {
			ui.picture.setImage(image);
		}
		if(uii != null) {
			uii.picture.setImage(image);
		}
		if(uiat != null) {
			uiat.picture.setImage(image);
		}
		if(uiati != null) {
			uiati.picture.setImage(image);
		}
		if(chat != null) {
			chat.item.picture.setImage(image);
		}
	}
	
	public String getDisplayName() {
		return displayName;
	}

	
	public void delete_nickname(ActionEvent e) {
		nickname.set("");
	}
	
	public void delete(ActionEvent e) {
		conversation.clear();
		server.serverView.remove(ui);
		Main.static_all_chats.getChildren().remove(uiat);
	}
	
	public void block(ActionEvent e) {
		JSONObject m = new JSONObject();
		try{
			m.put("type", MessageType.user_block).put("userID_to_block", userID);
			isBlocked.set(true);
			server.send(m);
		}catch(JSONException e2) {
			e2.printStackTrace();
		}
	}

	public void remove_contact(ActionEvent e) {
		
	}
}
