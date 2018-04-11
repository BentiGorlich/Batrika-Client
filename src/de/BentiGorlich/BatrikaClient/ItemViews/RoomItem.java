package de.BentiGorlich.BatrikaClient.ItemViews;

import de.BentiGorlich.BatrikaBasic.Helper;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.Room;
import de.BentiGorlich.BatrikaClient.Basic.TextMessage;
import de.BentiGorlich.BatrikaClient.Basic.User;
import de.BentiGorlich.BatrikaClient.Windows.RoomChat;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class RoomItem extends Item{
	
	Room r;
	public boolean isInfoItem;
	
	public RoomItem(Room r, boolean isInfoItem) {
		super();
		this.r = r;
		this.isInfoItem = isInfoItem;
		if(r.getImage() != null) {
			picture.setImage(r.getImage());
		}
		this.topLabel.setText(r.name);
		
		if(isInfoItem) {
			r.members.addListener(new ListChangeListener<User>() {
				@Override
				public void onChanged(Change<? extends User> val) {
					val.next();
					String member = "";
					for(int i = 0; i<r.members.size(); i++) {
						member += r.members.get(i).getDisplayName() + ", ";
					}
					bottomLeftLabel.setText(member + "me");
				}
			});
			String member = "";
			for(int i = 0; i<r.members.size(); i++) {
				member += r.members.get(i).getDisplayName() + ", ";
			}
			member += "me";
			
			
			bottomLeftLabel.setText(member);
			GridPane.setColumnSpan(bottomLeftLabel, 2);
			bottomLeftLabel.setVisible(true);
			bottomRightLabel.getParent().setVisible(false);
		}else {
			
			bottomRightLabel.getParent().setVisible(false);
			bottomRightLabel.textProperty().bind(r.unseenMessages.asString());
			
			r.lastMessage.addListener(new ChangeListener<TextMessage>() {
				@Override
				public void changed(ObservableValue<? extends TextMessage> val, TextMessage arg1, TextMessage arg2) {
					TextMessage tm = val.getValue();
					if(val.getValue().fromMe) {
						bottomLeftLabel.setText(Helper.getTime(tm.time_sent) + " me: " + tm.message);
					}else {
						bottomLeftLabel.setText(Helper.getTime(tm.time_sent) + " " + tm.getDisplayName() + ": " + tm.message);
					}
					LastMessage = tm;
				}
			});
			r.unseenMessages.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> val, Number arg1, Number arg2) {
					if(val.getValue().intValue() > 0) {
						if(Main.static_content.getChildren().contains(r.chat)) {
							r.unseenMessages.set(0);
						}else {
							bottomRightLabel.getParent().setVisible(true);
						}
					}else {
						bottomRightLabel.getParent().setVisible(false);
					}
				}
			});
		}
	}
	
	@Override
	protected void populateContextMenu() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(click && mouse) {
			r.unseenMessages.set(0);
			setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, new Insets(0))));
			mask.setImage(mask_mouse_over);
			click = false;
			if(Main.static_main.getDividerPositions()[0] > 0.8) {
				Main.static_main.setDividerPositions(Main.db.divider_pos);
			}else {
				Main.db.divider_pos = Main.static_main.getDividerPositions()[0];
			}
			if(r.chat == null) {
				r.chat = new RoomChat(r, r.server);
			}
			r.chat.seenMessages();
			if(Main.static_content.getChildren().size()>0) {
				Node onBack = Main.static_content.getChildren().get(0);
				r.chat.setBackTo(onBack);
			}
			Main.static_content.getChildren().clear();
			Main.static_content.getChildren().add(r.chat);
			System.out.println("computing message width...");
			for(int i = 0; i<r.chat.mis.size(); i++) {
				r.chat.mis.get(i).computeWrap();
			}
			((ScrollPane)r.chat.main.getCenter()).setVvalue(1.0);
		}
	}

}
