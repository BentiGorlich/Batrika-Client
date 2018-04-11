package de.BentiGorlich.BatrikaClient.Windows;

import java.util.Calendar;
import java.util.List;

import javafx.collections.ListChangeListener;
import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaClient.Categories;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import de.BentiGorlich.BatrikaClient.Basic.TextMessage;
import de.BentiGorlich.BatrikaClient.Basic.User;
import de.BentiGorlich.BatrikaClient.ItemViews.DetailUserItem;
import de.BentiGorlich.BatrikaClient.ItemViews.MessageItem;
import de.BentiGorlich.BatrikaClient.ItemViews.UserItem;
import de.BentiGorlich.BatrikaClient.ItemViews.UserItemAllTab;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class UserChat extends Chat{
	
	User user;
	
	public UserChat(User user, Server server){
		super(server, new UserItem(user, true));
		this.user = user;
		for(int i = 0; i<user.conversation.size(); i++) {
			TextMessage curr = user.conversation.get(i);
			addMessage(curr);
		}
		user.conversation.addListener((ListChangeListener<? super TextMessage>)this::addMessage);
		content.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				((ScrollPane)main.getCenter()).setVvalue(1.0);
			}
		});
	
	}
	/*
	public void addMessage(Change<? extends TextMessage> m) {
		m.next();
		if(m.wasAdded()) {
			List<? extends TextMessage> temp = m.getAddedSubList();
			for(int i = 0; i<temp.size();i++){
				TextMessage curr = temp.get(i);
				if(Main.static_content.getChildren().contains(this)) {
					curr.seen = true;
				}
				addMessage(curr);
			}
		}else if(m.wasRemoved()){
			List<? extends TextMessage> temp = m.getRemoved();
			for(int i = 0; i<temp.size();i++){
				TextMessage curr = temp.get(i);
				for(int j = 0; j<mis.size(); j++) {
					MessageItem curr_mi = mis.get(j);
					if(curr_mi.textMessage.equals(curr)) {
						content.getChildren().remove(curr_mi);
						mis.remove(j);
						break;
					}
				}
			}
		}
	}*/
	public void seenMessages() {
		for(int i = 0; i<unseenMessages.size(); i++) {
			TextMessage tm = unseenMessages.get(i); 
			if(tm != null) {
				if(tm.sender != null && !tm.fromMe) {
					tm.seen = true;
					JSONObject message = new JSONObject();
					try {
						message
							.put("type", MessageType.message_seen.toInt())
							.put("destID", tm.sender.userID)
							.put("category", Categories.user.toInt())
							.put("sendersID", tm.sendersMessageID)
						;
						server.send(message);
					} catch(JSONException e) {
						e.printStackTrace();
					}
					System.out.println("seen message: " + tm.message + "(" + tm.myMessageID + ")");
				}
			}
		}
		unseenMessages.clear();
	}
	public void mouseReleased(MouseEvent e) {
		if(item.click && item.mouse) {
			item.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, new Insets(0))));
			item.click = false;
			Main.static_content.getChildren().clear();
			DetailUserItem di = new DetailUserItem(user, server);
			di.setBackTo(this);
			Main.static_content.getChildren().add(di);
		}
	}
	public void addMessage(TextMessage m) {
		MessageItem mi = new MessageItem(m, this.widthProperty());
		mi.selected.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> val, Boolean arg1, Boolean arg2) {
				if(val.getValue() == true) {
					selectedMessages.add(mi);
				}else {
					selectedMessages.remove(mi);
				}
			}
		});
		mis.add(mi);
		content.getChildren().add(mi);
		if(!m.seen && !m.fromMe) {
			unseenMessages.add(m);
			user.unseenMessages.set(user.unseenMessages.get() + 1);
		}else if(!m.fromMe){
			JSONObject m_seen = new JSONObject();
			try {
				m_seen
					.put("type", MessageType.message_seen.toInt())
					.put("sendersID", m.sendersMessageID)
					.put("category", Categories.user.toInt())
					.put("destID", m.sender.userID)
				;
				server.send(m_seen);
			}catch(JSONException e) {
				e.printStackTrace();
			}
		}
		mi.computeWrap();
	}
	public void send(ActionEvent e) {
		if(!message.getText().equals("") && server.isConnected && server.isLoggedin) {
			if(user.conversation.size() == 0){
				UserItem ui = new UserItem(user, false);
				user.ui = ui;
				server.serverView.add(ui);
				UserItemAllTab uiat = new UserItemAllTab(user, server, false);
				user.uiat = uiat;
				Main.static_all_chats.getChildren().add(uiat);
			}
			JSONObject m = new JSONObject();
			try {
				TextMessage newMessage = new TextMessage(message.getText(), Calendar.getInstance(), server.getNextMessageID(), null, true);
				m
					.put("message", newMessage.message)
					.put("messageID", newMessage.myMessageID)
					.put("type", MessageType.message.toInt())
					.put("timestamp", newMessage.getTimeInMillis())
					.put("origin_type", Categories.user.toInt())
					.put("dest", user.userID)
				;
				server.send(m);
				user.addMessage(newMessage);
				message.setText("");
				Main.static_all_chats.getChildren().remove(user.ui);
				Main.static_all_chats.getChildren().add(0, user.ui);
				server.serverView.setToFront(user.ui);
			}catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}
}
