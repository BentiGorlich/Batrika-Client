package de.BentiGorlich.BatrikaClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaBasic.Helper;
import de.BentiGorlich.BatrikaBasic.ImageButton;
import de.BentiGorlich.BatrikaBasic.Visibility;
import de.BentiGorlich.BatrikaClient.Basic.CalendarUser;
import de.BentiGorlich.BatrikaClient.Basic.Room;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import de.BentiGorlich.BatrikaClient.Basic.TextMessage;
import de.BentiGorlich.BatrikaClient.Basic.User;
import de.BentiGorlich.BatrikaClient.ItemViews.*;
import de.BentiGorlich.BatrikaClient.Windows.*;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DataBase {
	final File ClientProperties = new File(Paths.get("res", "client.cfg").toString());
	public final File ServerDirectory = new File(Paths.get("res", "server").toString());
	public ArrayList<Server> servers = new ArrayList<Server>();
	boolean isFullscreen;
	Double x_position;
	Double y_position;
	Double width;
	Double height;
	public Double divider_pos;
	public boolean isMaximized;
	
	public ImageButton imgb_add_room;

	public void readServers() {
		
		String[]  server_files_s = ServerDirectory.list();
		Image normal = new Image("file:" + Paths.get("res", "pictures", "buttons", "add_white", "normal.png").toString());
		Image mouse_over = new Image("file:" + Paths.get("res", "pictures", "buttons", "add_white", "mouse_over.png").toString());
		Image clicked = new Image("file:" + Paths.get("res", "pictures", "buttons", "add_white", "clicked.png").toString());
		for(int i = 0; i<server_files_s.length; i++) {
			try {
				File server_file = new File(ServerDirectory.getAbsolutePath() + "\\" + server_files_s[i]);
				Server curr_server = new Server();
				File config_f = new File(server_file.getAbsolutePath() + "\\server.cfg");
				BufferedReader bf = new BufferedReader(new FileReader(config_f));
				String line, allLines = "";
				while((line = bf.readLine()) != null) {
					allLines += line;
				}
				bf.close();
				JSONObject config = new JSONObject(allLines);
				curr_server.Servername = config.getString("name");
				curr_server.password = config.getString("password");
				curr_server.address = config.getString("address");
				curr_server.port = config.getInt("port");
				
				if(!config.has("accountname") || !config.has("userpassword")) {
					curr_server.canLogin = false;
				}else {
					curr_server.setAccountname(config.getString("accountname"));
					curr_server.userpassword = config.getString("userpassword");
					curr_server.canLogin = true;
					curr_server.setDisplayname(config.getString("displayname"));
					curr_server.email = config.getString("email");
					curr_server.visiblility = Visibility.fromInt(config.getInt("visibility"));
					curr_server.userID = config.getInt("userID");
					File ownpic = new File(server_file.getAbsolutePath() + "\\profile.png");
					if(ownpic.exists()) {
						curr_server.profile_picture = new Image("file:" + ownpic.getAbsolutePath());
					}else {
						curr_server.profile_picture = Main.standard;
					}
					curr_server.setID(config.getInt("nextMessageID"));
				}
				
				ServerView curr_serverview = new ServerView(curr_server);
				curr_server.serverView = curr_serverview;
				Tab curr_tab = new Tab(curr_server.Servername, new AnchorPane());
				curr_server.server_tab = curr_tab;

				ContextMenu server_context = new ContextMenu();
				MenuItem reconnect = new MenuItem("reconnect");
				reconnect.setOnAction(curr_server::reconnect);
				server_context.getItems().add(reconnect);
				MenuItem disconnect = new MenuItem("disconnect");
				disconnect.setOnAction(curr_server::disconnect);
				server_context.getItems().add(disconnect);
				MenuItem delete = new MenuItem("delete");
				delete.setOnAction(curr_server::deleteServer);
				server_context.getItems().add(delete);
				MenuItem rename = new MenuItem("rename");
				rename.setOnAction(curr_server::renameServer);
				server_context.getItems().add(rename);
				
				curr_tab.setContextMenu(server_context);
				
				AnchorPane.setBottomAnchor(curr_serverview, 0.0);
				AnchorPane.setTopAnchor(curr_serverview, 0.0);
				AnchorPane.setLeftAnchor(curr_serverview, 0.0);
				AnchorPane.setRightAnchor(curr_serverview, 0.0);
				
				((AnchorPane)curr_tab.getContent()).getChildren().add(curr_serverview);
				
				Main.static_server.getTabs().add(curr_tab);
				curr_tab.setGraphic(new Circle(7.0, Color.RED));
				
				//user
				
				File[] users = new File(server_file.getAbsolutePath() + "\\users").listFiles();
				for(int j = 0; j<users.length; j++) {
					bf = new BufferedReader(new FileReader(users[j]));
					allLines = "";
					while((line = bf.readLine()) != null) {
						allLines += line;
					}
					bf.close();
					JSONObject curr_user_json = new JSONObject(allLines);
					String name = curr_user_json.getString("name");
					int id = curr_user_json.getInt("userID");
					User curr_user = new User(name, id);
					if(curr_user_json.has("nickname")) {
						curr_user.nickname.set(curr_user_json.getString("nickname"));
					}
					curr_user.isContact = curr_user_json.getBoolean("isContact");
					File pp = new File(server_file.getAbsolutePath() + "\\profile_pictures\\" + id + ".png");
					if(pp.exists()) {
						curr_user.profile_picture = new Image("file:" + pp.getAbsolutePath());
					}else {
						curr_user.profile_picture = Main.standard;
					}
					
					//messages
					
					JSONArray m_jsona = curr_user_json.getJSONArray("messages");
					if(m_jsona.length()>0) {
						UserItem curr_ui = new UserItem(curr_user, false);
						curr_user.ui = curr_ui;
						curr_serverview.add(curr_ui);
						UserItemAllTab uiat = new UserItemAllTab(curr_user, curr_server, false); 
						curr_user.uiat = uiat;
						Main.static_all_chats.getChildren().add(curr_user.uiat);
					}
					for(int k = 0; k<m_jsona.length(); k++) {
						JSONObject m_json = m_jsona.getJSONObject(k);
						Calendar timestamp = Calendar.getInstance();
						timestamp.setTimeInMillis(m_json.getLong("timestamp"));
						TextMessage m_tmp = new TextMessage(m_json.getString("message"), timestamp, m_json.getInt("id"), curr_user, m_json.getBoolean("from_me"));
						m_tmp.seen = m_json.getBoolean("seen");
						m_tmp.status = MessageStatus.fromInt(m_json.getInt("status"));
						if(m_json.has("receivedUser")) {
							Calendar time = Calendar.getInstance();
							time.setTimeInMillis(m_json.getLong("receivedUser"));
							m_tmp.time_receivedByUser.add(new CalendarUser(curr_user.userID, time));
						}
						if(m_json.has("received")) {
							m_tmp.time_uploadedToServer = Calendar.getInstance();
							m_tmp.time_uploadedToServer.setTimeInMillis(m_json.getLong("received"));
						}
						if(m_json.has("seenByUser")) {
							Calendar time = Calendar.getInstance();
							time.setTimeInMillis(m_json.getLong("seenByUser"));
							m_tmp.time_seenByUser.add(new CalendarUser(curr_user.userID, time));
						}
						curr_user.addMessage(m_tmp);
					}
					curr_user.conversation.sort(Comparator.comparing(TextMessage::getTimeInMillis));
					
					JSONArray oldnames = curr_user_json.getJSONArray("oldnames");
					for(int k = 0; k<oldnames.length(); k++) {
						curr_user.oldnames.add(oldnames.getString(k));
					}
					
					curr_server.users.add(curr_user);
					if(curr_user.isContact) {
						curr_server.contacts.add(curr_user);
						UserItem curr_uii = new UserItem(curr_user, true);
						curr_user.uii = curr_uii;
						curr_serverview.add(curr_uii);
						
						UserItemAllTab uiati  = new UserItemAllTab(curr_user, curr_server, true);
						curr_user.uiati = uiati;
						Main.static_all_user.getChildren().add(curr_user.uiati);
					}
					
					curr_user.chat = new UserChat(curr_user, curr_server);
				}
				
				File[] blockedusers = new File(server_file.getAbsolutePath() + "\\blocked").listFiles();
				for(int j = 0; j<blockedusers.length; j++) {
					int id = Integer.parseInt(blockedusers[j].getName());
					curr_server.blockedIDs.add(id);
				}

				imgb_add_room = new ImageButton(normal, mouse_over, clicked, this::createRoom, 30.0, 30.0);
				imgb_add_room.accessibleTextProperty().set(curr_server.Servername);
				curr_server.serverView.addToRooms(imgb_add_room);
				curr_server.addRoom = imgb_add_room;
				
				File[]rooms = new File(server_file.getAbsolutePath() + "\\rooms").listFiles();
				for(int j = 0; j<rooms.length; j++) {
					bf = new BufferedReader(new FileReader(rooms[j]));
					allLines = "";
					while((line = bf.readLine()) != null) {
						allLines += line;
					}
					bf.close();
					JSONObject curr_room_json = new JSONObject(allLines);
					String roomname = curr_room_json.getString("roomname");
					Integer ownerID = curr_room_json.getInt("ownerID");
					JSONArray members_json = curr_room_json.getJSONArray("members");
					JSONArray conversation_json = curr_room_json.getJSONArray("conversation");
					Room curr_room = new Room(roomname, curr_server);
					if(ownerID != curr_server.userID) {
						curr_room.owner = curr_server.getUser(ownerID);
					}else {
						curr_room.owner = new User(curr_server.getAccountName(), curr_server.userID);
					}
					
					for(int k = 0; k<members_json.length(); k++) {
						JSONObject curr_member_json = members_json.getJSONObject(k);
						int userID = curr_member_json.getInt("userID");
						User curr_member = curr_server.getUser(userID);
						if(curr_member != null) {
							curr_room.members.add(curr_member);
						}else if(userID != curr_server.userID){
							String displayname = curr_member_json.getString("displayName");
							curr_room.members.add(new User(displayname, userID));
						}
					}
					for(int k = 0; k<conversation_json.length(); k++) {
						JSONObject curr_message_json = conversation_json.getJSONObject(k);
						String message = curr_message_json.getString("message");
						Calendar timestamp = Calendar.getInstance();
						timestamp.setTimeInMillis(curr_message_json.getLong("timestamp"));
						int id = curr_message_json.getInt("messageID");
						boolean me = curr_message_json.getBoolean("fromMe");
						User sender = null;
						if(curr_message_json.has("sendersUserID")) {
							sender = curr_room.getUser(curr_message_json.getInt("sendersUserID"));
						}
						TextMessage curr_message = new TextMessage(message, timestamp, id, sender, me);
						curr_message.seen = curr_message_json.getBoolean("seenByMe");
						if(curr_message_json.has("receivedByUser")) {
							JSONArray receivedByUser = curr_message_json.getJSONArray("receivedByUser");
							for(int l = 0; l<receivedByUser.length(); l++) {
								JSONObject curr_received = receivedByUser.getJSONObject(l);
								Calendar curr_time = Calendar.getInstance();
								curr_time.setTimeInMillis(curr_received.getLong("timestamp"));
								CalendarUser cu = new CalendarUser(curr_received.getInt("userID"), curr_time);
								curr_message.time_receivedByUser.add(cu);
							}
						}
						if(curr_message_json.has("seenByUser")) {
							JSONArray receivedByUser = curr_message_json.getJSONArray("seenByUser");
							for(int l = 0; l<receivedByUser.length(); l++) {
								JSONObject curr_received = receivedByUser.getJSONObject(l);
								Calendar curr_time = Calendar.getInstance();
								curr_time.setTimeInMillis(curr_received.getLong("timestamp"));
								CalendarUser cu = new CalendarUser(curr_received.getInt("userID"), curr_time);
								curr_message.time_receivedByUser.add(cu);
							}
						}
						curr_message.status = MessageStatus.fromInt(curr_message_json.getInt("status"));
						curr_room.addMessage(curr_message);
					}
					curr_room.rii = new RoomItem(curr_room, true);
					curr_server.serverView.add(curr_room.rii);
					curr_room.riiat = new RoomItemAllTab(curr_room, curr_server, true);
					Main.static_all_rooms.getChildren().add(curr_room.riiat);
					curr_room.chat = new RoomChat(curr_room, curr_server);
					curr_server.rooms.add(curr_room);
				}
				servers.add(curr_server);
				Main.static_cb_server.getItems().add(curr_server.Servername);
				ArrayList<Item> chats_to_sort = new ArrayList<Item>();
				ObservableList<Node> children = curr_server.serverView.getChats();
				for(int j = 0; j<children.size(); j++) {
					try{
						chats_to_sort.add((Item)children.get(j));
					}catch(ClassCastException e) {
						e.printStackTrace();
					}
				}
				chats_to_sort.sort(Comparator.comparingLong(Item::getTimeFromLastMessage).reversed());
				curr_server.serverView.setChats(chats_to_sort);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ArrayList<Item> chats_to_sort = new ArrayList<Item>();
		for(int i = 0; i<Main.static_all_chats.getChildren().size(); i++) {
			try{
				chats_to_sort.add((Item)Main.static_all_chats.getChildren().get(i));
			}catch(ClassCastException e) {
				e.printStackTrace();
			}
		}
		chats_to_sort.sort(Comparator.comparingLong(Item::getTimeFromLastMessage).reversed());
		Main.static_all_chats.getChildren().clear();
		for(int i = 0; i<chats_to_sort.size(); i++) {
			Main.static_all_chats.getChildren().add(chats_to_sort.get(i));
		}
		imgb_add_room = new ImageButton(normal, mouse_over, clicked, this::createRoomAllServer, 30.0, 30.0);
		Main.static_all_rooms.getChildren().add(imgb_add_room);
	}

	public void readClient() {
		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader(ClientProperties));
			String line, allLines = "";
			while((line = bf.readLine()) != null) {
				allLines += line;
			}
			bf.close();
			JSONObject config = new JSONObject(allLines);
			x_position = config.getDouble("x-position");
			y_position = config.getDouble("y-position");
			width = config.getDouble("width");
			height = config.getDouble("height");
			isFullscreen = config.getBoolean("fullscreen");
			isMaximized = config.getBoolean("maximized");
			divider_pos = config.getDouble("divider_pos");
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setupServers() {
		ServerDirectory.mkdirs();
		File offical_server = new File(ServerDirectory.getAbsolutePath() + "\\Offical Batrika Server");
		offical_server.mkdir();
		File users = new File(offical_server.getAbsolutePath() + "\\users");
		users.mkdir();
		File rooms = new File(offical_server.getAbsolutePath() + "\\rooms");
		rooms.mkdir();
		File blocked = new File(offical_server.getAbsolutePath() + "\\blocked");
		blocked.mkdir();
		File profile_pictures = new File(offical_server.getAbsolutePath() + "\\profile_pictures");
		profile_pictures.mkdir();
		JSONObject server_cfg = new JSONObject();
		try {
			server_cfg
				.put("name", "Offical Batrika Server")
				.put("address", "localhost")
				.put("port", 22500)
				.put("password", "")
			;
			BufferedWriter bfw = new BufferedWriter(new FileWriter(offical_server.getAbsolutePath() + "\\server.cfg"));
			bfw.write(Helper.JsonToString(server_cfg));
			bfw.close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setupClient() {
		JSONObject config = new JSONObject();
		try {
			config
				.put("x-position", 0.0)
				.put("y-position", 0.0)
				.put("height", 600.0)
				.put("width", 400.0)
				.put("fullscreen", false)
				.put("maximized", false)
				.put("divider_pos", 0.5)
			;
			ClientProperties.getParentFile().mkdirs();
			ClientProperties.createNewFile();
			BufferedWriter bfw = new BufferedWriter(new FileWriter(ClientProperties));
			bfw.write(config.toString());
			bfw.close();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void save() {
		System.out.println("saving db...");
		if(Main.static_main.getDividerPositions()[0] < 0.8) {
			divider_pos = Main.static_main.getDividerPositions()[0];
		}
		System.out.println("saving client...");
		//Client
		JSONObject config = new JSONObject();
		try {
			config
				.put("x-position", x_position)
				.put("y-position", y_position)
				.put("height", height)
				.put("width", width)
				.put("fullscreen", isFullscreen)
				.put("maximized", isMaximized)
				.put("divider_pos", divider_pos)
			;
			BufferedWriter bfw = new BufferedWriter(new FileWriter(ClientProperties));
			bfw.write(Helper.JsonToString(config));
			bfw.close();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//server
		System.out.println("saving server...");
		for(int i = 0; i<servers.size(); i++) {
			Server curr_server = servers.get(i);
			System.out.println("saving server \"" + curr_server.Servername + "\"...");
			try {
				curr_server.connection.close();
			} catch (IOException | NullPointerException e) {
				//e.printStackTrace();
			}
			File curr_server_file = new File(this.ServerDirectory + "\\" + curr_server.Servername);
			if(!curr_server_file.exists()) {
				curr_server_file.mkdirs();
			}
			
			//server.cfg
			
			JSONObject server_cfg = new JSONObject();
			try {
				server_cfg
					.put("name", curr_server.Servername)
					.put("password", curr_server.password)
					.put("accountname", curr_server.getAccountName())
					.put("displayname", curr_server.getDisplayname())
					.put("email", curr_server.email)
					.put("userpassword", curr_server.userpassword)
					.put("address", curr_server.address)
					.put("port", curr_server.port)
					.put("nextMessageID", curr_server.getNextMessageID())
					.put("visibility", curr_server.visiblility.toInt())
					.put("userID", curr_server.userID)
				;
				BufferedWriter bfw = new BufferedWriter(new FileWriter(curr_server_file + "\\server.cfg"));
				bfw.write(Helper.JsonToString(server_cfg));
				bfw.close();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//user

			System.out.println("saving user on \"" + curr_server.Servername + "\"...");
			File curr_users_file = new File(curr_server_file.getAbsolutePath() + "\\users");
			if(!curr_users_file.exists()) {
				curr_users_file.mkdirs();
			}
			for(int j = 0; j<curr_server.contacts.size(); j++) {
				User curr_user = curr_server.contacts.get(j);
				System.out.println("saving user\"" + curr_user.getDisplayName() + "\"...");
				File curr_user_file = new File(curr_users_file.getAbsolutePath() + "\\" + curr_user.userID + ".user");
				JSONArray messages = new JSONArray();
				for(int k = 0; k<curr_user.conversation.size(); k++) {
					TextMessage curr_m = curr_user.conversation.get(k);
					JSONObject m = new JSONObject();
					try {
						m
							.put("timestamp", curr_m.time_sent.getTimeInMillis())
							.put("message", curr_m.message)
							.put("id", curr_m.myMessageID)
							.put("from_me", curr_m.fromMe)
							.put("seen", curr_m.seen)
							.put("status", curr_m.status.toInt())
						;
						if(curr_m.time_uploadedToServer != null) {
							m.put("received", curr_m.time_uploadedToServer.getTimeInMillis());
						}
						if(curr_m.time_receivedByUser.size()>0) {
							m.put("receivedUser", curr_m.time_receivedByUser.get(0).time.getTimeInMillis());
						}
						if(curr_m.time_seenByUser.size()>0) {
							m.put("seenByUser", curr_m.time_seenByUser.get(0).time.getTimeInMillis());
						}
						messages.put(m);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				JSONArray oldnames = new JSONArray();
				for(int k = 0; k<curr_user.oldnames.size(); k++) {
					oldnames.put(curr_user.oldnames.get(k));
				}
				if(!curr_user_file.exists()) {
					try {
						curr_user_file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				JSONObject user = new JSONObject();
				try {
					user
						.put("name", curr_user.getDisplayName())
						.put("isContact", curr_user.isContact)
						.put("userID", curr_user.userID)
						.put("nickname", curr_user.nickname.get())
						.put("messages", messages)
						.put("oldnames", oldnames)
					;
					BufferedWriter bfw = new BufferedWriter(new FileWriter(curr_user_file));
					bfw.write(Helper.JsonToString(user));
					bfw.close();
				} catch(IOException | JSONException e) {
					e.printStackTrace();
				}
			}
			
			//blocked user
			curr_users_file = new File(curr_server_file.getAbsoluteFile() + "\\blocked");
			for(int j = 0; j<curr_server.blockedIDs.size(); j++) {
				Integer id = curr_server.blockedIDs.get(j);
				File curr_user_file = new File(curr_users_file.getAbsoluteFile() + "\\" + id + ".user");
				if(!curr_user_file.exists()) {
					try {
						curr_user_file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			//rooms
			//TODO
			System.out.println("saving rooms...");
			for(int j = 0; j<curr_server.rooms.size(); j++) {
				Room curr_room = curr_server.rooms.get(j);
				File curr_room_dir = new File(curr_server_file + "\\rooms");
				if(!curr_room_dir.exists()) {
					curr_room_dir.mkdir();
				}
				File curr_room_cfg = new File(curr_room_dir.getAbsolutePath() + "\\" + curr_room.name + ".cfg");
				JSONObject room_config = new JSONObject();
				try {
					JSONArray members = new JSONArray();
					for(int k = 0; k<curr_room.members.size(); k++) {
						User curr_member = curr_room.members.get(k);
						JSONObject curr_member_json = new JSONObject();
						if(curr_server.getUser(curr_member.userID) != null) {
							curr_member_json.put("userID", curr_member.userID);
						}else {
							curr_member_json
								.put("userID", curr_member.userID)
								.put("displayName", curr_member.getDisplayName())
							;
						}
						members.put(curr_member_json);
					}
					JSONArray conversation = new JSONArray();
					for(int k = 0; k<curr_room.conversation.size(); k++) {
						TextMessage tm = curr_room.conversation.get(k);
						JSONArray received = new JSONArray();
						for(int l = 0; l<tm.time_receivedByUser.size(); l++) {
							JSONObject curr_time_received = new JSONObject();
							CalendarUser cu = tm.time_receivedByUser.get(l);
							curr_time_received.put("userID", cu.userID);
							curr_time_received.put("timestamp", cu.time.getTimeInMillis());
							received.put(curr_time_received);
							
						}
						JSONArray seen = new JSONArray();
						for(int l = 0; l<tm.time_seenByUser.size(); l++) {
							JSONObject curr_time_seen = new JSONObject();
							CalendarUser cu = tm.time_seenByUser.get(l);
							curr_time_seen.put("userID", cu.userID);
							curr_time_seen.put("timestamp", cu.time.getTimeInMillis());
							seen.put(curr_time_seen);
						}
						JSONObject curr_room_message = new JSONObject();
						curr_room_message
							.put("message", tm.message)
							.put("timestamp", tm.getTimeInMillis())
							.put("fromMe", tm.fromMe)
							.put("seenByMe", tm.seen)
							.put("messageID", tm.myMessageID)
							.put("sendersMessageID", tm.sendersMessageID)
							.put("receivedByUser", received)
							.put("seenByUser", seen)
							.put("status", tm.status.toInt())
						;
						if(tm.sender != null) {
							curr_room_message.put("sendersUserID", tm.sender.userID);
						}
						if(!tm.fromMe && tm.sender != null) {
							curr_room_message
								.put("userID", tm.sender.userID)
							;
						}
						conversation.put(curr_room_message);
					}
					room_config
						.put("roomname", curr_room.name)
						.put("members", members)
						.put("conversation", conversation)
						.put("ownerID", curr_room.owner.userID)
					;
					if(!curr_room_cfg.exists()) {
						curr_room_cfg.createNewFile();
					}
					BufferedWriter bfw = new BufferedWriter(new FileWriter(curr_room_cfg));
					bfw.write(Helper.JsonToString(room_config));
					bfw.close();
					if(curr_room.getImage() != null) {
						if(!curr_room.getImage().equals(Main.standard)) {
							File curr_room_pp = new File(curr_room_dir.getAbsolutePath() + "\\" + curr_room.name + ".png");
							if(!curr_room_pp.exists()) {
								curr_room_pp.createNewFile();
							}
							ImageIO.write(SwingFXUtils.fromFXImage(curr_room.getImage(), null), "png", curr_room_pp);
						}
					}
					
				} catch(JSONException | IOException e) {
					e.printStackTrace();
				}
			}
			//groups
			//TODO
			
		}
	}
	
	public Server addServer(String servername, String hostaddress, String pw, int port) {
		Server server = new Server();
		server.Servername = servername;
		server.address = hostaddress;
		server.port = port;
		server.password = pw;
		ServerView sv = new ServerView(server);
		server.serverView = sv;
		Tab servertab = new Tab(servername, new AnchorPane());
		Main.static_server.getTabs().add(servertab);
		servertab.setGraphic(new Circle(7.0, Color.RED));
		server.server_tab = servertab;
		Main.static_cb_server.getItems().add(servername);
		servers.add(server);

		Image normal = new Image("file:" + Paths.get("res", "pictures", "buttons", "add_white", "normal.png").toString());
		Image mouse_over = new Image("file:" + Paths.get("res", "pictures", "buttons", "add_white", "mouse_over.png").toString());
		Image clicked = new Image("file:" + Paths.get("res", "pictures", "buttons", "add_white", "clicked.png").toString());
		
		imgb_add_room = new ImageButton(normal, mouse_over, clicked, this::createRoom, 30.0, 30.0);
		imgb_add_room.accessibleTextProperty().set(server.Servername);
		server.serverView.addToRooms(imgb_add_room);
		server.addRoom = imgb_add_room;
		
		ContextMenu server_context = new ContextMenu();
		MenuItem reconnect = new MenuItem("reconnect");
		reconnect.setOnAction(server::reconnect);
		server_context.getItems().add(reconnect);
		MenuItem disconnect = new MenuItem("disconnect");
		disconnect.setOnAction(server::disconnect);
		server_context.getItems().add(disconnect);
		MenuItem delete = new MenuItem("delete");
		delete.setOnAction(server::deleteServer);
		server_context.getItems().add(delete);
		servertab.setContextMenu(server_context);
		MenuItem rename = new MenuItem("rename");
		rename.setOnAction(server::renameServer);
		server_context.getItems().add(rename);
		
		AnchorPane.setBottomAnchor(sv, 0.0);
		AnchorPane.setTopAnchor(sv, 0.0);
		AnchorPane.setLeftAnchor(sv, 0.0);
		AnchorPane.setRightAnchor(sv, 0.0);
		
		((AnchorPane)servertab.getContent()).getChildren().add(sv);
		
		
		File curr_serverdir = new File(ServerDirectory.getAbsolutePath() + "\\" + servername);
		File users = new File(curr_serverdir.getAbsolutePath() + "\\users");
		File blocked = new File(curr_serverdir.getAbsolutePath() + "\\blocked");
		File rooms = new File(curr_serverdir.getAbsolutePath() + "\\rooms");
		File groups = new File(curr_serverdir.getAbsolutePath() + "\\groups");
		File profile_pictures = new File(curr_serverdir.getAbsolutePath() + "\\profile_pictures");
		users.mkdirs();
		blocked.mkdirs();
		rooms.mkdirs();
		groups.mkdirs();
		profile_pictures.mkdirs();
		return server;
	}
	
	private void createRoomAllServer(ActionEvent e) {
		createRoomAllServer temp = new createRoomAllServer();
		if(Main.static_content.getChildren().size()>0) {
			temp.setBackTo(Main.static_content.getChildren().get(0));
		}
		Main.static_content.getChildren().clear();
		Main.static_content.getChildren().add(temp);
	}
	
	private void createRoom(ActionEvent e) {
		String name = Main.static_server.getSelectionModel().getSelectedItem().getText();
		System.out.println(name);
		createRoom(name);
	}
	
	public void createRoom(String servername) {
		createRoom temp = new createRoom(servername);
		if(Main.static_content.getChildren().size()>0) {
			temp.setBackTo(Main.static_content.getChildren().get(0));
		}
		Main.static_content.getChildren().clear();
		Main.static_content.getChildren().add(temp);
	}
	
	public Server getServer(String servername) {
		for(int i = 0; i<servers.size(); i++) {
			Server curr = servers.get(i);
			if(curr.Servername.equals(servername)) {
				return curr;
			}
		}
		return null;
	}
	
	public boolean hasServer(String hostname) {
		for(int i = 0; i<servers.size(); i++) {
			if(servers.get(i).address.equals(hostname)) {
				return true;
			}
		}
		return false;
	}
}
