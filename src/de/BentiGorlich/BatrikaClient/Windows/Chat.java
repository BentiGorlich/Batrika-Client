package de.BentiGorlich.BatrikaClient.Windows;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import de.BentiGorlich.BatrikaBasic.ImageButton;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import de.BentiGorlich.BatrikaClient.Basic.TextMessage;
import de.BentiGorlich.BatrikaClient.ItemViews.Item;
import de.BentiGorlich.BatrikaClient.ItemViews.MessageItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public abstract class Chat extends BorderPane{

	Server server;
	Parent p;
	Node backTo = null;
	ArrayList<TextMessage> unseenMessages = new ArrayList<TextMessage>();
	public ArrayList<MessageItem> mis = new ArrayList<MessageItem>();
	public ObservableList<MessageItem> selectedMessages = FXCollections.observableArrayList();
	
	GridPane topOneMessageSelected;
	FlowPane top_one_buttons = new FlowPane();
	ImageButton imgb_one_back;
	ImageButton imgb_one_info;
	ImageButton imgb_one_share;
	
	GridPane topMultipleMessagesSelected;
	FlowPane top_multiple_buttons = new FlowPane();
	ImageButton imgb_multiple_back;
	ImageButton imgb_multiple_share;
	
	@FXML
	VBox content;
	@FXML
	TextField message;
	@FXML
	GridPane name;
	@FXML
	public BorderPane main;
	@FXML
	GridPane bottom;
	
	ImageButton imgb;
	ImageButton imgb_send;
	
	
	public Item item;
	
	public Chat(Server s, Item i) {
		item = i;
		this.server = s;
		AnchorPane.setBottomAnchor(this, 0.0);
		AnchorPane.setTopAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 0.0);
		AnchorPane.setRightAnchor(this, 0.0);
		
		selectedMessages.addListener(this::selectionChanged);
		
		RowConstraints row1 = new RowConstraints();
		row1.setFillHeight(false);
		row1.setMinHeight(50);
		row1.setMaxHeight(50.0);
		row1.setPrefHeight(50.0);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setFillWidth(false);
		c1.setHgrow(Priority.NEVER);
		c1.setMinWidth(50.0);
		c1.setMaxWidth(50.0);
		c1.setPrefWidth(50.0);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setFillWidth(true);
		c2.setHgrow(Priority.ALWAYS);
		c2.setMinWidth(USE_COMPUTED_SIZE);
		c2.setPrefWidth(USE_COMPUTED_SIZE);
		c2.setMaxWidth(USE_COMPUTED_SIZE);
		
		top_one_buttons.setAlignment(Pos.CENTER_RIGHT);
		top_multiple_buttons.setAlignment(Pos.CENTER_RIGHT);
		
		topOneMessageSelected = new GridPane();
		topOneMessageSelected.addRow(0);
		topOneMessageSelected.addColumn(0);
		topOneMessageSelected.addColumn(1);
		topOneMessageSelected.getRowConstraints().add(0, row1);
		topOneMessageSelected.getColumnConstraints().add(0, c1);
		topOneMessageSelected.getColumnConstraints().add(1, c2);
		
		topOneMessageSelected.add(top_one_buttons, 1, 0);

		topMultipleMessagesSelected = new GridPane();
		topMultipleMessagesSelected.addRow(0);
		topMultipleMessagesSelected.addColumn(0);
		topMultipleMessagesSelected.addColumn(1);
		topMultipleMessagesSelected.getRowConstraints().add(0, row1);
		topMultipleMessagesSelected.getColumnConstraints().add(0, c1);
		topMultipleMessagesSelected.getColumnConstraints().add(1, c2);
		
		topMultipleMessagesSelected.add(top_multiple_buttons, 1, 0);
		
		FXMLLoader loader;
		try {
			
			loader = new FXMLLoader(Paths.get("res", "layouts", "chat.fxml").toUri().toURL());
			loader.setController(this);
			p = loader.load();
			
			Image normal = new Image(Paths.get("res", "pictures", "Buttons", "back_white", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			Image MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "back_white", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			Image clicked = new Image(Paths.get("res", "pictures", "Buttons", "back_white", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			
			imgb = new ImageButton(normal, MouseOver, clicked, this::back, 50.0, 50.0);
			imgb_one_back = new ImageButton(normal, MouseOver, clicked, this::setTopStandard, 50, 50);
			imgb_multiple_back = new ImageButton(normal, MouseOver, clicked, this::setTopStandard, 50, 50);
			
			topOneMessageSelected.add(imgb_one_back, 0, 0);
			topMultipleMessagesSelected.add(imgb_multiple_back, 0, 0);
			
			normal = new Image(Paths.get("res", "pictures", "Buttons", "share_white", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "share_white", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "share_white", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			imgb_multiple_share = new ImageButton(normal, MouseOver, clicked, this::share, 50.0, 50.0);
			imgb_one_share = new ImageButton(normal, MouseOver, clicked, this::share, 50.0, 50.0);
			
			top_one_buttons.getChildren().add(imgb_one_share);
			top_multiple_buttons.getChildren().add(imgb_multiple_share);
			
			normal = new Image(Paths.get("res", "pictures", "Buttons", "info_white", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "info_white", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "info_white", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			imgb_one_info = new ImageButton(normal, MouseOver, clicked, this::info, 50.0, 50.0);
			
			top_one_buttons.getChildren().add(imgb_one_info);

			normal = new Image(Paths.get("res", "pictures", "Buttons", "send_white", "normal.png").toUri().toURL().toString(), 512, 512, true, true);
			MouseOver = new Image(Paths.get("res", "pictures", "Buttons", "send_white", "mouse_over.png").toUri().toURL().toString(), 512, 512, true, true);
			clicked = new Image(Paths.get("res", "pictures", "Buttons", "send_white", "clicked.png").toUri().toURL().toString(), 512, 512, true, true);
			imgb_send = new ImageButton(normal, MouseOver, clicked, this::send, 30, 30);
			bottom.add(imgb_send, 1, 0);
			
			imgb.setSmooth(true);
			imgb_multiple_back.setSmooth(true);
			imgb_multiple_share.setSmooth(true);
			imgb_one_back.setSmooth(true);
			imgb_one_info.setSmooth(true);
			imgb_send.setSmooth(true);

			item.setOnMouseReleased(this::mouseReleased);
			name.add(item, 1, 0);
			name.add(imgb, 0, 0);
			
			AnchorPane.setBottomAnchor(main, 0.0);
			AnchorPane.setTopAnchor(main, 0.0);
			AnchorPane.setLeftAnchor(main, 0.0);
			AnchorPane.setRightAnchor(main, 0.0);
			this.setCenter(main);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		private void share(ActionEvent e) {
			
		}
		
		private void info(ActionEvent e) {
			new MessageInfo(selectedMessages.get(0).textMessage);
		}
		
		public void setBackTo(Node backTo) {
			this.backTo = backTo;
		}
		
		private void setTopStandard(ActionEvent e) {
			for(int i = 0; selectedMessages.size()>0;) {
				selectedMessages.get(i).unselect();
			}
			setTopStandard();
		}
		
		private void setTopStandard() {
			main.getChildren().remove(main.getTop());
			main.setTop(name);
		}
		
		private void setTopOneItemSelected() {
			main.getChildren().remove(main.getTop());
			main.setTop(topOneMessageSelected);
		}
		
		private void setTopMultipleItemsSelected() {
			main.getChildren().remove(main.getTop());
			main.setTop(topMultipleMessagesSelected);
		}
		
		private void selectionChanged(Change<? extends MessageItem> changed) {
			if(selectedMessages.size() == 0) {
				setTopStandard();
			}else if(selectedMessages.size() == 1) {
				setTopOneItemSelected();
			}else if(selectedMessages.size() > 1) {
				setTopMultipleItemsSelected();
			}
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
		
		@FXML
		public abstract void send(ActionEvent e);
		
		public abstract void addMessage(TextMessage m);
		
		public abstract void addMessage(Change<? extends TextMessage> m);
		
		public abstract void seenMessages() ;
		
		public abstract void mouseReleased(MouseEvent e) ;
}
