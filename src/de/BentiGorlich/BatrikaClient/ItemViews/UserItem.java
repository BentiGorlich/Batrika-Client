package de.BentiGorlich.BatrikaClient.ItemViews;

import de.BentiGorlich.BatrikaBasic.Helper;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.TextMessage;
import de.BentiGorlich.BatrikaClient.Basic.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class UserItem extends Item{
	
	User user;
	Label nickname;
	public boolean isInfoItem = false;
	
	public UserItem(User user, boolean isInfoItem){
		super();
		this.user = user;
		this.isInfoItem = isInfoItem;
		if(user.profile_picture != null) {
			picture.setImage(user.profile_picture);
		}
		topLabel.setText(user.getDisplayName());
		topLabel.visibleProperty().bind(user.nickname.isEmpty());
		GridPane.setColumnSpan(topLabel, 2);
		
		nickname = new Label();
		nickname.textProperty().bind(user.nickname);
		nickname.visibleProperty().bind(user.nickname.isEmpty().not());
		main.add(nickname, 1, 0);
		GridPane.setColumnSpan(nickname, 2);
		if(!isInfoItem) {
			user.lastMessage.addListener(new ChangeListener<TextMessage>() {
				@Override
				public void changed(ObservableValue<? extends TextMessage> val, TextMessage arg1, TextMessage arg2) {
					TextMessage tm = val.getValue();
					if(tm.fromMe) {
						bottomLeftLabel.setText(Helper.getTime(tm.time_sent) + " me: " + tm.message);
					}else {
						bottomLeftLabel.setText(Helper.getTime(tm.time_sent) + " >>: " + tm.message);
					}
					LastMessage = tm;
				}
			});
			
			bottomRightLabel.textProperty().bind(user.unseenMessages.asString());
			
			
			user.unseenMessages.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> val, Number arg1, Number arg2) {
					if(val.getValue().intValue() > 0) {
						if(Main.static_content.getChildren().contains(user.chat)) {
							user.unseenMessages.set(0);
						}else {
							bottomRightLabel.getParent().setVisible(true);
						}
					}else {
						bottomRightLabel.getParent().setVisible(false);
					}
				}
			});
		}else {
			GridPane.setColumnSpan(topLabel, 2);
			GridPane.setRowSpan(topLabel, 2);
			
			GridPane.setRowSpan(nickname, 2);
		}
	}
	
	
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(click && mouse) {
			user.unseenMessages.set(0);
			setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, new Insets(0))));
			mask.setImage(mask_mouse_over);
			click = false;
			if(Main.static_main.getDividerPositions()[0] > 0.8) {
				Main.static_main.setDividerPositions(Main.db.divider_pos);
			}else {
				Main.db.divider_pos = Main.static_main.getDividerPositions()[0];
			}
			user.chat.seenMessages();
			if(Main.static_content.getChildren().size()>0) {
				Node onBack = Main.static_content.getChildren().get(0);
				user.chat.setBackTo(onBack);
			}
			Main.static_content.getChildren().clear();
			Main.static_content.getChildren().add(user.chat);
			System.out.println("computing message width...");
			for(int i = 0; i<user.chat.mis.size(); i++) {
				user.chat.mis.get(i).computeWrap();
			}
			((ScrollPane)user.chat.main.getCenter()).setVvalue(1.0);
		}
	}

	@Override
	protected void populateContextMenu() {
		MenuItem block = new MenuItem("block");
		block.setOnAction(user::block);
		context.getItems().add(block);
		MenuItem remove = new MenuItem("remove Contact");
		remove.setOnAction(user::remove_contact);
		context.getItems().add(remove);
		MenuItem delete = new MenuItem("delete Conversation");
		delete.setOnAction(user::delete);
		context.getItems().add(delete);
		MenuItem delete_nick = new MenuItem("delete Nickname");
		delete_nick.setOnAction(user::delete_nickname);
		context.getItems().add(delete_nick);
	}
}
