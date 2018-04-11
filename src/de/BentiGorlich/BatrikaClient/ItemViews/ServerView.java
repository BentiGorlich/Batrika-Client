package de.BentiGorlich.BatrikaClient.ItemViews;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.TextFieldSuggestionField;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import de.BentiGorlich.BatrikaClient.Basic.User;
import de.BentiGorlich.BatrikaClient.Windows.UserChat;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ServerView extends BorderPane{
	
	public Server server;
	public ObservableList<Node> user_childs;
	
	public TextFieldSuggestionField add_room = new TextFieldSuggestionField();
	
	private TabPane mid = new TabPane();
	private Tab user = new Tab("User", new AnchorPane());
	private Tab conversations = new Tab("Chats", new AnchorPane());
	private Tab groups = new Tab("Groups", new AnchorPane());
	private Tab rooms = new Tab("Rooms", new AnchorPane());
	
	public UserItem top;
	public User me;
	
	private VBox user_v;
	private VBox conversations_v;
	private VBox groups_v;
	private VBox rooms_v;
	
	public ServerView(Server server){
		this.server = server;
		user_v = new VBox();
		user_childs = user_v.getChildren();
		conversations_v = new VBox();
		groups_v = new VBox();
		rooms_v = new VBox();
		
		me = new User("me", server.userID);
		
		if(server.profile_picture != null) {
			me.profile_picture = server.profile_picture;
		}else {
			me.profile_picture = Main.standard;
		}
		
		top = new UserItem(me, true);
		top.setOnMouseReleased(this::detail);
		
		top.user.setName(server.getDisplayname() + "(" + server.getAccountName() + ")"); 
		top.user.uii = top;
		top.user.chat = new UserChat(top.user, server);
		top.setTopText(top.user.getDisplayName());
		this.setTop(top);
		this.setCenter(mid);
		
		
		ScrollPane sp1 = new ScrollPane(new AnchorPane());
		sp1.setFitToWidth(true);
		ScrollPane sp2 = new ScrollPane(new AnchorPane());
		sp2.setFitToWidth(true);
		ScrollPane sp3 = new ScrollPane(new AnchorPane());
		sp3.setFitToWidth(true);
		ScrollPane sp4 = new ScrollPane(new AnchorPane());
		sp4.setFitToWidth(true);
		
		AnchorPane.setBottomAnchor(sp1, 0.0);
		AnchorPane.setTopAnchor(sp1, 0.0);
		AnchorPane.setLeftAnchor(sp1, 0.0);
		AnchorPane.setRightAnchor(sp1, 0.0);
		
		AnchorPane.setBottomAnchor(sp2, 0.0);
		AnchorPane.setTopAnchor(sp2, 0.0);
		AnchorPane.setLeftAnchor(sp2, 0.0);
		AnchorPane.setRightAnchor(sp2, 0.0);
		
		AnchorPane.setBottomAnchor(sp3, 0.0);
		AnchorPane.setTopAnchor(sp3, 0.0);
		AnchorPane.setLeftAnchor(sp3, 0.0);
		AnchorPane.setRightAnchor(sp3, 0.0);

		AnchorPane.setBottomAnchor(sp4, 0.0);
		AnchorPane.setTopAnchor(sp4, 0.0);
		AnchorPane.setLeftAnchor(sp4, 0.0);
		AnchorPane.setRightAnchor(sp4, 0.0);
		
		((AnchorPane)sp1.getContent()).getChildren().add(conversations_v);
		((AnchorPane)sp2.getContent()).getChildren().add(user_v);
		((AnchorPane)sp3.getContent()).getChildren().add(rooms_v);
		((AnchorPane)sp4.getContent()).getChildren().add(groups_v);
		
		AnchorPane.setLeftAnchor(user_v, 0.0);
		AnchorPane.setRightAnchor(user_v, 0.0);

		AnchorPane.setLeftAnchor(groups_v, 0.0);
		AnchorPane.setRightAnchor(groups_v, 0.0);

		AnchorPane.setLeftAnchor(conversations_v, 0.0);
		AnchorPane.setRightAnchor(conversations_v, 0.0);

		AnchorPane.setLeftAnchor(rooms_v, 0.0);
		AnchorPane.setRightAnchor(rooms_v, 0.0);
		
		user_v.setFillWidth(true);
		groups_v.setFillWidth(true);
		conversations_v.setFillWidth(true);
		rooms_v.setFillWidth(true);
		
		((AnchorPane)conversations.getContent()).getChildren().add(sp1);
		((AnchorPane)user.getContent()).getChildren().add(sp2);
		((AnchorPane)rooms.getContent()).getChildren().add(sp3);
		((AnchorPane)groups.getContent()).getChildren().add(sp4);
		
		mid.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		mid.getTabs().add(conversations);
		mid.getTabs().add(user);
		mid.getTabs().add(rooms);
		mid.getTabs().add(groups);
		
		VBox.setMargin(add_room, new Insets(10));
		rooms_v.getChildren().add(add_room);
		add_room.setPromptText("add room");
		add_room.setOnKeyTyped(this::searchRooms);
	}
	
	private void searchRooms(KeyEvent e) {
		String roomname = add_room.getText() + e.getCharacter();
		roomname = roomname.replaceAll("\\p{C}", "");
		JSONObject search_room = new JSONObject();
		try {
			search_room
				.put("type", MessageType.room_search.toInt())
				.put("roomname", roomname)
			;
			server.send(search_room);
		} catch(JSONException e2) {
			e2.printStackTrace();
		}
	}
	
	public ObservableList<Node> getChats(){
		return conversations_v.getChildren();
	}
	
	public void setChats(ArrayList<Item> chats_to_sort) {
		conversations_v.getChildren().clear();
		for(int i = 0; i<chats_to_sort.size(); i++) {
			conversations_v.getChildren().add(i, chats_to_sort.get(i));
		}
	}
	
	public void setToFront(Node n) {
		conversations_v.getChildren().remove(n);
		conversations_v.getChildren().add(0, n);
	}
	
	public void addToChats(Node n) {
		conversations_v.getChildren().add(n);
	}
	public void addToUser(Node n) {
		user_v.getChildren().add(n);
	}
	public void addToRooms(Node n) {
		rooms_v.getChildren().add(n);
	}
	public void addToGroups(Node n) {
		groups_v.getChildren().add(n);
	}
	public void add(UserItem ui) {
		if(!ui.isInfoItem) {
			conversations_v.getChildren().add(ui);
		}else {
			user_v.getChildren().add(ui);
		}
	}

	public void add(RoomItem ri) {
		if(!ri.isInfoItem) {
			conversations_v.getChildren().add(ri);
		}else {
			rooms_v.getChildren().remove(server.addRoom);
			rooms_v.getChildren().add(ri);
			rooms_v.getChildren().add(server.addRoom);
		}
	}
	
	public void add(GroupItem g) {
		
	}

	public void add(User curr_user) {
		UserItem uii = new UserItem(curr_user, true);
		curr_user.uii = uii;
		user_v.getChildren().add(uii);
		if(curr_user.conversation.size()>0) {
			UserItem ui = new UserItem(curr_user, false);
			curr_user.ui = ui;
			this.conversations_v.getChildren().add(curr_user.ui);
		}
	}

	public void remove(UserItem ui) {
		if(!ui.isInfoItem) {
			conversations_v.getChildren().remove(ui);
		}else {
			user_v.getChildren().remove(ui);
		}
	}
	
	public void remove(RoomItem ri) {
		if(!ri.isInfoItem) {
			conversations_v.getChildren().remove(ri);
		}else {
			rooms_v.getChildren().remove(ri);
		}
	}
	
	public void remove(GroupItem g) {
		
	}

	public void remove(User curr_user) {
		if(curr_user.ui != null) {
			remove(curr_user.ui);
		}if(curr_user.uii != null) {
			remove(curr_user.uii);
		}
	}
	public void setDisplayname(String displayname) {
		top.user.setName(displayname + "(" + server.getAccountName() + ")");
		top.setTopText(top.user.getDisplayName());
	}
	
	public void setImage(Image i) {
		top.setImage(i);
	}
	
	public Server getServer() {
		return server;
	}
	
	private void detail(MouseEvent e) {
		if(top.click && top.mouse) {
			top.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, new Insets(0))));
			top.click = false;
			if(Main.static_main.getDividerPositions()[0] > 0.8) {
				Main.static_main.setDividerPositions(Main.db.divider_pos);
			}else {
				Main.db.divider_pos = Main.static_main.getDividerPositions()[0];
			}
			Main.static_content.getChildren().clear();
			Main.static_content.getChildren().add(new MeItem(server));
			System.out.println("released");
		}
	}
}
