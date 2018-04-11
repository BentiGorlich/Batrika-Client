package de.BentiGorlich.BatrikaClient.Basic;

import java.util.ArrayList;
import java.util.List;

import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.ItemViews.RoomItem;
import de.BentiGorlich.BatrikaClient.ItemViews.RoomItemAllTab;
import de.BentiGorlich.BatrikaClient.Windows.RoomChat;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class Room extends MultiUser{
	public String name;
	public ObservableList<User> members = FXCollections.observableArrayList();
	public ObservableList<TextMessage> conversation = FXCollections.observableArrayList();
	public User owner;
	public RoomItem ri;
	public RoomItem rii;
	public RoomItemAllTab riiat;
	public RoomItemAllTab riat;
	private Image picture;
	public ArrayList<User> admins = new ArrayList<User>();
	public RoomChat chat;
	public Server server;
	public SimpleIntegerProperty unseenMessages = new SimpleIntegerProperty(0);
	public SimpleIntegerProperty messagesReceived = new SimpleIntegerProperty(0);
	public SimpleIntegerProperty messagesSent = new SimpleIntegerProperty(0);
	public SimpleObjectProperty<TextMessage> lastMessage = new SimpleObjectProperty<TextMessage>();
	
	public Room(String name, Server s) {
		this.name = name;
		server = s;
	}
	
	public Image getImage() {
		return picture;
	}
	
	public void setImage(Image image) {
		picture = image;
		ri.setImage(image);
	}
	
	public User getUser(int userID) {
		for(int i = 0; i<members.size(); i++) {
			User curr = members.get(i);
			if(curr.userID == userID) {
				return curr;
			}
		}
		return null;
	}
	
	public boolean contains(User u) {
		if(members.contains(u)) {
			return true;
		}
		return false;
	}
	
	public void addMessage(TextMessage newMessage) {
		if(ri == null) {
			ri = new RoomItem(this, false);
			server.serverView.add(ri);
		}
		if(riat == null) {
			riat = new RoomItemAllTab(this, server, false);
			Main.static_all_chats.getChildren().add(riat);
		}
		if(newMessage.fromMe) {
			messagesSent.set(messagesSent.get() + 1);
		}else {
			messagesReceived.set(messagesReceived.get() + 1);
		}
		conversation.add(newMessage);
		lastMessage.set(newMessage);
	}

	@Override
	public List<User> getMembers() {
		return members.subList(0, members.size());
	}
}
