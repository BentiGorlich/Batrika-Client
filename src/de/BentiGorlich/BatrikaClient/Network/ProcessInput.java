
package de.BentiGorlich.BatrikaClient.Network;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.MessageStatus;
import de.BentiGorlich.BatrikaBasic.Helper;
import de.BentiGorlich.BatrikaBasic.MediaFragmented;
import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaBasic.Visibility;
import de.BentiGorlich.BatrikaClient.Categories;
import de.BentiGorlich.BatrikaClient.Basic.CalendarUser;
import de.BentiGorlich.BatrikaClient.Basic.Room;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import de.BentiGorlich.BatrikaClient.Basic.TextMessage;
import de.BentiGorlich.BatrikaClient.Basic.User;
import de.BentiGorlich.BatrikaClient.ItemViews.RoomItem;
import de.BentiGorlich.BatrikaClient.ItemViews.RoomItemAllTab;
import de.BentiGorlich.BatrikaClient.ItemViews.SearchItem;
import de.BentiGorlich.BatrikaClient.ItemViews.SearchRoomItem;
import de.BentiGorlich.BatrikaClient.ItemViews.SearchUserItem;
import de.BentiGorlich.BatrikaClient.ItemViews.UserItem;
import de.BentiGorlich.BatrikaClient.ItemViews.UserItemAllTab;
import de.BentiGorlich.BatrikaClient.Windows.UserChat;
import de.BentiGorlich.BatrikaClient.Windows.Login;
import de.BentiGorlich.BatrikaClient.Windows.RoomInsertPW;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ProcessInput implements EventHandler<WorkerStateEvent>{
	Server server;
	InputStreamListener isl;
	DataInputStream in;
	int lastMediaNr = 0;
	ArrayList<MediaFragmented> media = new ArrayList<MediaFragmented>();
	
	public ProcessInput(Server server, DataInputStream in){
		this.server = server;
		this.in = in;
		start(in);
	}

	private void start(DataInputStream in) {
		isl = new InputStreamListener(in);
		isl.setOnSucceeded(this);
		isl.setOnFailed(this::failed);
		Thread t = new Thread(isl);
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void handle(WorkerStateEvent val) {
		try {
			JSONObject in = new JSONObject(isl.getMessage());
			MessageType input = MessageType.get(in.getInt("type"));
			if(input != MessageType.profile_picture) {
				System.out.println("processing: " + isl.getMessage());
				System.out.println(val.getEventType());
			}else {
				if(!in.has("initialize") && !in.has("completed")) {
					try{
						System.out.println("processing: pp \"" + in.getString("name") + "\", from user \"" + in.getInt("userID") + "\", nr: " + in.getInt("media_Nr"));
					}catch(JSONException e) {	}
				}else {
					System.out.println("processing: " + isl.getMessage());
				}
				System.out.println(val.getEventType());
			}
			switch (input) {
			case selfinfo:
				if(in.getString("accountname").equals(server.getAccountName())) {
					if(in.has("username")){
						server.setDisplayname(in.getString("username"));
					}
					if(in.has("email")){
						server.email = in.getString("email");
					}
					server.userID = in.getInt("userID");
					server.serverView.setImage(server.profile_picture);
					if(in.has("pp_checksum")) {
						File pp = new File(Main.db.ServerDirectory + "\\" + server.Servername + "\\profile.png");
						String checksum = "";
						try {
							checksum = Helper.getFileChecksum(MessageDigest.getInstance("SHA-1"), pp);
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						if(!checksum.equals(in.getString("pp_checksum")) || !pp.exists()) {
							requestPP(server.userID);
						}
					}
					if(in.has("visibility")) {
						server.visiblility = Visibility.fromInt(in.getInt("visibility"));
					}
				}
				break;
			case message:
				if(in.has("message") && in.has("origin_type") && in.has("timestamp") && in.has("userID") && in.has("sendersID")) {
					Calendar time = Calendar.getInstance();
					time.setTimeInMillis(in.getLong("timestamp"));
					User sender = server.getUser(in.getInt("userID"));
					Categories ot = Categories.getFromInt(in.getInt("origin_type"));
					TextMessage temp_m = new TextMessage(
							in.getString("message"),
							time,
							server.getNextMessageID(),
							sender, 
							false
						);
					temp_m.sendersMessageID = in.getInt("sendersID");
					switch(ot) {
					case user:
						JSONObject m_received = new JSONObject();
						if(sender == null) {
							if(in.has("username")) {
								sender = new User(in.getString("username"), in.getInt("userID"));
								sender.addMessage(temp_m);
								UserItem uii = new UserItem(sender, true);
								sender.uii = uii;
								server.serverView.add(uii);
								UserItemAllTab uiati = new UserItemAllTab(sender, server, true);
								sender.uiati = uiati;
								Main.static_all_user.getChildren().add(uiati);
								sender.chat = new UserChat(sender, server);
								server.contacts.add(sender);
								requestPP(sender.userID);
							}
						}
						m_received
							.put("type", MessageType.message_received_user.toInt())
							.put("sendersID", temp_m.sendersMessageID)
							.put("category", Categories.user.toInt())
							.put("destID", sender.userID)
						;
						server.send(m_received);
						if(sender.ui == null) {
							sender.ui = new UserItem(sender, false);
							server.serverView.add(sender.ui);
						}
						if(sender.uiat == null) {
							sender.uiat = new UserItemAllTab(sender, server, false);
							Main.static_all_chats.getChildren().add(sender.uiat);
						}
						sender.addMessage(temp_m);
						((ScrollPane)sender.chat.main.getCenter()).setVvalue(1.0);
						Main.static_all_chats.getChildren().remove(sender.uiat);
						Main.static_all_chats.getChildren().add(0, sender.uiat);
						server.serverView.setToFront(sender.ui);
						break;
					case room:
						if(in.has("roomname")) {
							if(sender == null) {
								if(in.has("displayname")){
									sender = new User(in.getString("displayname"), in.getInt("userID"));
								}
							}
							Room r = server.getRoom(in.getString("roomname"));
							if(r != null) {
								if(r.chat != null) {
									m_received = new JSONObject();
									m_received
										.put("type", MessageType.message_received_user.toInt())
										.put("sendersID", temp_m.sendersMessageID)
										.put("dest", r.name)
										.put("category", Categories.room.toInt())
										.put("roomname", r.name)
										.put("destID", sender.userID)
									;
									server.send(m_received);
									r.addMessage(temp_m);
									((ScrollPane)r.chat.main.getCenter()).setVvalue(1.0);
									Main.static_all_chats.getChildren().remove(r.riat);
									Main.static_all_chats.getChildren().add(0, r.riat);
									server.serverView.setToFront(r.ri);
								}
							}
						}
						break;
					default:
						break;
					}
				}
				break;
			case message_received:
				if(in.has("messageID") && in.has("userID") && in.has("category")) {
					int messageID = in.getInt("messageID");
					int userID = in.getInt("userID");
					TextMessage tm = null;
					User curr = server.getUser(userID);
					Categories category = Categories.getFromInt(in.getInt("category"));
					switch (category) {
					case user:
						for(int i = 0; i<curr.conversation.size(); i++) {
							TextMessage curr_tm = curr.conversation.get(i);
							if(curr_tm.myMessageID == messageID) {
								tm = curr_tm;
								break;
							}
						}
						break;
					case room:
						if(in.has("roomname")) {
							String roomname = in.getString("roomname");
							Room r = server.getRoom(roomname);
							if(r != null) {
								for(int i = 0; i<r.conversation.size(); i++) {
									TextMessage tm_temp = r.conversation.get(i);
									if(tm_temp.myMessageID == messageID) {
										tm = tm_temp;
										break;
									}
								}
							}
						}
						break;
					default:
						break;
					}
					if(tm != null) {
						if(tm.status == MessageStatus.none) {
							tm.status = MessageStatus.received_by_server;
							tm.mi.editStatus();
							if(in.has("timestamp")) {
								tm.time_uploadedToServer = Calendar.getInstance();
								tm.time_uploadedToServer.setTimeInMillis(in.getLong("timestamp"));
							}
						}
					}
				}
				break;
			case message_received_user:
				if(in.has("messageID") && in.has("userID") && in.has("category")) {
					int messageID = in.getInt("messageID");
					int userID = in.getInt("userID");
					TextMessage tm = null;
					User curr = server.getUser(userID);
					Categories category = Categories.getFromInt(in.getInt("category"));
					switch(category) {
					case user:
						for(int i = 0; i<curr.conversation.size(); i++) {
							TextMessage curr_tm = curr.conversation.get(i);
							if(curr_tm.myMessageID == messageID) {
								tm = curr_tm;
								break;
							}
						}
						if(tm != null) {
							if(tm.status == MessageStatus.received_by_server) {
								tm.status = MessageStatus.received_by_user;
								tm.mi.editStatus();
								if(in.has("timestamp")) {
									Calendar time = Calendar.getInstance();
									time.setTimeInMillis(in.getLong("timestamp"));
									tm.time_receivedByUser.add(0, new CalendarUser(userID, time));
								}
							}
						}
						break;
					case room:
						if(in.has("roomname")) {
							String roomname = in.getString("roomname");
							Room r = server.getRoom(roomname);
							if(r != null) {
								for(int i = 0; i<r.conversation.size(); i++) {
									TextMessage tm_temp = r.conversation.get(i);
									if(tm_temp.myMessageID == messageID) {
										tm = tm_temp;
										break;
									}
								}
								if(tm != null) {
									if(tm.status == MessageStatus.received_by_server) {
										if(in.has("timestamp")) {
											Calendar time = Calendar.getInstance();
											time.setTimeInMillis(in.getLong("timestamp"));
											tm.time_receivedByUser.add(new CalendarUser(userID, time));
											if(tm.time_receivedByUser.size() == r.members.size()) {
												tm.status = MessageStatus.received_by_user;
												tm.mi.editStatus();
											}
										}
									}
								}
							}
						}
						break;
					default:
						break;
					}
				}
				break;
			case message_seen:
				if(in.has("messageID") && in.has("userID") && in.has("category")) {
					Categories category = Categories.getFromInt(in.getInt("category"));
					int messageID = in.getInt("messageID");
					int userID = in.getInt("userID");
					TextMessage tm = null;
					User curr = server.getUser(userID);
					switch(category) {
					case user:
						for(int i = 0; i<curr.conversation.size(); i++) {
							TextMessage curr_tm = curr.conversation.get(i);
							if(curr_tm.myMessageID == messageID) {
								tm = curr_tm;
								break;
							}
						}
						if(tm != null) {
							if(tm.status == MessageStatus.received_by_user) {
								tm.status = MessageStatus.seen_by_client;
								tm.mi.editStatus();
								if(in.has("timestamp")) {
									Calendar time = Calendar.getInstance();
									time.setTimeInMillis(in.getLong("timestamp"));
									tm.time_seenByUser.add(0, new CalendarUser(userID, time));
								}
							}
						}
						break;
					case room:
						if(in.has("roomname")) {
							String roomname = in.getString("roomname");
							Room r = server.getRoom(roomname);
							if(r != null) {
								for(int i = 0; i<r.conversation.size(); i++) {
									TextMessage tm_temp = r.conversation.get(i);
									if(tm_temp.myMessageID == messageID) {
										tm = tm_temp;
										break;
									}
								}
								if(tm != null) {
									if(tm.status == MessageStatus.received_by_user) {
										if(in.has("timestamp")) {
											Calendar time = Calendar.getInstance();
											time.setTimeInMillis(in.getLong("timestamp"));
											tm.time_seenByUser.add(new CalendarUser(userID, time));
											if(tm.time_seenByUser.size() == r.members.size()) {
												tm.status = MessageStatus.seen_by_client;
												tm.mi.editStatus();
											}
										}
									}
								}
							}
						}
						break;
					default:
						break;
					}
				}
				break;
			case login:
				server.server_tab.setGraphic(new Circle(7.0, Color.GREEN));
				server.isLoggedin = true;
				break;
			case contact_add:
				if(in.has("username") && in.has("userID")) {
					User n = new User(in.getString("username"), in.getInt("userID"));
					server.addContact(n);
					requestPP(n.userID);
				}
				break;
			case user_info:
				if(in.has("username") && in.has("userID")) {
					User curr = server.getUser(in.getInt("userID"));
					if(curr != null) {
						if(!curr.getDisplayName().equals(in.getString("username"))) {
							curr.setName(in.getString("username"));
						}
						if(in.has("pp_checksum") && in.getInt("userID") != server.userID) {
							String checksum = "";
							if(curr.profile_picture != null) {
								if(!curr.profile_picture.equals(Main.standard)) {
									File pp = new File(Main.db.ServerDirectory.getAbsolutePath() + "\\" + server.Servername + "\\profile_pictures\\" + curr.userID + ".png");
									try {
										checksum = Helper.getFileChecksum(MessageDigest.getInstance("SHA-1"), pp);
									} catch (NoSuchAlgorithmException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
							if(!checksum.equals(in.getString("pp_checksum"))) {
								requestPP(curr.userID);
							}
						}
						if(curr.uii == null) {
							curr.uii = new UserItem(curr, true);
							server.serverView.add(curr.uii);
						}
						if(curr.uiati == null) {
							curr.uiati = new UserItemAllTab(curr, server, true);
							Main.static_all_user.getChildren().add(curr.uiati);
						}
						if(curr.conversation.size()>0) {
							if(curr.ui == null) {
								curr.ui = new UserItem(curr, false);
								server.serverView.add(curr.ui);
							}
							if(curr.uiat == null) {
								curr.uiat = new UserItemAllTab(curr, server, false);
								Main.static_all_chats.getChildren().add(curr.uiat);
							}
						}
					}else {
						User n = new User(in.getString("username"), in.getInt("userID"));
						server.addContact(n);
						requestPP(n.userID);
					}
				}else if(in.has("pp_checksum") && in.getInt("userID") == server.userID) {
					String checksum = "";
					File pp = new File(Main.db.ServerDirectory.getAbsolutePath() + "\\" + server.Servername + "\\profile.png");
					if(!server.profile_picture.equals(Main.standard)) {
						try {
							checksum = Helper.getFileChecksum(MessageDigest.getInstance("SHA-1"), pp);
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(!checksum.equals(in.getString("pp_checksum")) || !pp.exists()) {
						requestPP(server.userID);
					}
				}
				break;
			case profile_picture:
				if(in.has("initialize") && in.has("category") && in.has("total_size") && in.has("name")){
					if(in.getBoolean("initialize")) {
						long total = in.getLong("total_size");
						MediaFragmented mediafragment = new MediaFragmented(total);
						Categories cat = Categories.getFromInt(in.getInt("category"));
						switch(cat) {
						case user:
							if( in.has("userID")) {
								mediafragment.ID = in.getInt("userID");
								if(in.getInt("userID") != server.userID) {
									User curr = server.getUser(in.getInt("userID"));
									curr.setMinMax(0.0, total);
									curr.setLoadPP();
								}else {
									server.serverView.top.setMinMax(0.0, total);
									server.serverView.top.setLoadPP();
								}
							}
							break;
						case room:
							if(in.has("roomname")) {
								mediafragment.name = in.getString("roomname");
							}
							break;
						default:
							break;
						}
						mediafragment.Filename = in.getString("name");
						mediafragment.setInitialized(true);
						mediafragment.setCompleted(false);
						media.add(mediafragment);
					}
				}else if(in.has("category") && in.has("name") && !in.has("completed")) {
					MediaFragmented mediafragment = null;
					for(int i = 0; i<media.size(); i++) {
						MediaFragmented curr = media.get(i);
						if(curr.Filename.equals(in.getString("name"))) {
							mediafragment = curr;
							break;
						}
					}
					if(mediafragment != null) {
						if(mediafragment.isInitialized() && !mediafragment.isComplete()) {
							mediafragment.ID = in.getInt("userID");
							JSONArray bytearray = in.getJSONArray("bytes"); 
							mediafragment.add(bytearray);
							if(Categories.getFromInt(in.getInt("category")) == Categories.user) {
								if(server.userID != in.getInt("userID")) {
									User curr = server.getUser(in.getInt("userID"));
									curr.setProgress(mediafragment.currentSize);
								}else {
									server.serverView.top.setProgress(server.serverView.top.getProgress() + in.getJSONArray("bytes").length());
								}
							}
						}
					}
				}
				if(in.has("completed") && in.has("name") && in.has("category")) {
					MediaFragmented mediafragment = null;
					for(int i = 0; i<media.size(); i++) {
						MediaFragmented curr = media.get(i);
						if(curr.Filename.equals(in.getString("name"))) {
							mediafragment = curr;
							break;
						}
					}
					if(mediafragment != null) {
						mediafragment.setCompleted(in.getBoolean("completed"));
						if(mediafragment.isComplete()) {
							Categories category = Categories.getFromInt(in.getInt("category"));
							File f;
							if(category == Categories.user && in.has("userID")) {
								if(mediafragment.ID != server.userID) {
									f = new File(Main.db.ServerDirectory.getAbsolutePath() + "\\" + server.Servername + "\\profile_pictures\\" + mediafragment.ID + ".png");
								}else {
									f = new File(Main.db.ServerDirectory.getAbsolutePath() + "\\" + server.Servername + "\\profile.png");
								}
								System.out.println(f.getAbsolutePath());
								try {
									writeMediaFragmentToFile(f, mediafragment);
									if(mediafragment.ID != server.userID) {
										User curr = server.getUser(mediafragment.ID);
										curr.setProfilePicture(new Image("file:" + Main.db.ServerDirectory.getAbsolutePath() + "\\" + server.Servername + "\\profile_pictures\\" + mediafragment.ID + ".png"));
										curr.setProgressCompleted();
									}else {
										server.serverView.top.setImage(new Image("file:" + Main.db.ServerDirectory.getAbsolutePath() + "\\" + server.Servername + "\\profile.png"));
										server.serverView.top.setProgressCompleted();
										server.profile_picture = server.serverView.top.getImage(); 
									}
									mediafragment.setInitialized(false);
									mediafragment.setCompleted(true);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}else if(category == Categories.room && in.has("roomname")) {
								String roomname = in.getString("roomname");
								if(mediafragment.name.equals(roomname)) {
									f = new File(Main.db.ServerDirectory.getAbsolutePath() + "\\" + server.Servername + "\\rooms\\" + mediafragment.name + ".png");
									System.out.println(f.getAbsolutePath());
									try {
										writeMediaFragmentToFile(f, mediafragment);
										Room r = null;
										for(int j = 0; j<server.rooms.size(); j++) {
											Room curr = server.rooms.get(j);
											if(curr.name.equals(roomname)) {
												r = curr;
												break;
											}
										}
										r.setImage(new Image("file:" + f.getAbsolutePath()));
										mediafragment.setInitialized(false);
										mediafragment.setCompleted(true);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					
				}
				break;
			case user_block:
				
				break;
			case group_change:
				break;
			case room_change:
				break;
			case user_change:
				if(in.has("userID")) {
					if(in.has("username")) {
						String username = in.getString("username");
						User curr = server.getUser(in.getInt("userID")); 
						curr.setName(username);
					}
					if(in.has("pp_checksum")) {
						int userID = in.getInt("userID");
						File pp = new File(Main.db.ServerDirectory.getAbsolutePath() + "\\" + server.Servername + "\\profile_pictures\\" + userID + ".png");
						if(pp.exists()) {
							try {
								String checksum = Helper.getFileChecksum(MessageDigest.getInstance("SHA-1"), pp);
								if(!checksum.equals(in.getString("pp_checksum"))) {
									requestPP(userID);
								}
							} catch (NoSuchAlgorithmException | IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				break;
			case user_delete:
				break;
			case create_group:
				break;
			case group_join:
				break;
			case room_create:
				break;
			case user_create:
				break;
			case group_delete:
				break;
			case room_delete:
				break;
			case room_info:
				if(in.has("roomname")){
					String roomname = in.getString("roomname");
					if(!server.hasRoom(roomname)) {
						if(in.has("member") && in.has("ownerID") && in.has("admins")) {
							JSONArray member = in.getJSONArray("member");
							Integer ownersID = in.getInt("ownerID");
							String ownerName = in.getString("ownername");
							JSONArray admins = in.getJSONArray("admins");
							Room r;
							if(!server.hasRoom(roomname)) {
								r = new Room(roomname, server);
								server.rooms.add(r);
								r.rii = new RoomItem(r, true);
								server.serverView.add(r.rii);
								server.rooms.add(r);
								r.riiat = new RoomItemAllTab(r, server, true);
								Main.static_all_rooms.getChildren().add(r.riiat);
								Main.static_all_rooms.getChildren().remove(Main.db.imgb_add_room);
								Main.static_all_rooms.getChildren().add(Main.db.imgb_add_room);
							}
							r = server.getRoom(roomname);
							User owner = server.getUser(ownersID);
							if(owner == null) {
								owner = new User(ownerName, ownersID);
							}
							r.owner = owner;
							for(int i = 0; i<member.length(); i++) {
								JSONObject curr_user_json = member.getJSONObject(i);
								User curr_user = server.getUser(curr_user_json.getInt("userID"));
								if(curr_user == null && server.userID != curr_user_json.getInt("userID")) {
									curr_user = new User(curr_user_json.getString("username"), curr_user_json.getInt("userID"));
									server.addUser(curr_user);
								}
								if(curr_user != null) {
									r.members.add(curr_user);
									JSONObject m = new JSONObject();
									m
										.put("type", MessageType.profile_picture.toInt())
										.put("userID", curr_user.userID)
									;
								}
							}
							for(int i = 0; i<admins.length(); i++) {
								JSONObject curr_user_json = admins.getJSONObject(i);
								User curr_user = server.getUser(curr_user_json.getInt("userID"));
								r.admins.add(curr_user);
							}
						}
					}
				}
				break;
			case room_join:
				if(in.has("roomname") && in.has("selfjoin")) {
					String roomname = in.getString("roomname");
					if(in.getBoolean("selfjoin")){
						if(in.has("member") && in.has("ownerID") && in.has("admins")) {
							JSONArray member = in.getJSONArray("member");
							Integer ownersID = in.getInt("ownerID");
							String ownerName = in.getString("ownername");
							JSONArray admins = in.getJSONArray("admins");
							if(!server.hasRoom(roomname)) {
								Room r = new Room(roomname, server);
								server.rooms.add(r);
								User owner = server.getUser(ownersID);
								if(owner == null) {
									owner = new User(ownerName, ownersID);
								}
								r.owner = owner;
								for(int i = 0; i<member.length(); i++) {
									JSONObject curr_user_json = member.getJSONObject(i);
									User curr_user = server.getUser(curr_user_json.getInt("userID"));
									if(curr_user == null && server.userID != curr_user_json.getInt("userID")) {
										curr_user = new User(curr_user_json.getString("username"), curr_user_json.getInt("userID"));
										server.addUser(curr_user);
										r.members.add(curr_user);
										JSONObject m = new JSONObject();
										m
											.put("type", MessageType.profile_picture.toInt())
											.put("userID", curr_user.userID)
										;
									}
								}
								for(int i = 0; i<admins.length(); i++) {
									JSONObject curr_user_json = admins.getJSONObject(i);
									User curr_user = server.getUser(curr_user_json.getInt("userID"));
									r.admins.add(curr_user);
								}
								r.rii = new RoomItem(r, true);
								server.serverView.add(r.rii);
								server.rooms.add(r);
								r.riiat = new RoomItemAllTab(r, server, true);
								Main.static_all_rooms.getChildren().add(r.riiat);
							}
						}
					}
					
				}
				break;
			case room_search:
				if(in.has("rooms")) {
					List<SearchItem> receivedrooms = new ArrayList<SearchItem>();
					JSONArray rooms = in.getJSONArray("rooms");
					int i;
					for(i = 0; i<rooms.length(); i++) {
						JSONObject curr_room = rooms.getJSONObject(i);
						if(curr_room.has("roomname") && curr_room.has("hasPW")) {
							String roomname = curr_room.getString("roomname");
							boolean hasPW = curr_room.getBoolean("hasPW");
							receivedrooms.add(new SearchRoomItem(roomname, this::addRoom, hasPW));
							System.out.println(roomname);
						}
					}
					if(i>0) {
						server.serverView.add_room.populatePopup(receivedrooms);
						server.serverView.add_room.showPopup();
					}else {
						server.serverView.add_room.hidePopup();
					}
				}
				break;
			case media:
				break;
			case contact_remove:
				break;
			case user_unblock:
				break;
			case user_search:
				if(in.has("users")) {
					JSONArray users = in.getJSONArray("users");
					List<SearchItem> receivednames = new ArrayList<SearchItem>();
					int i;
					for(i = 0; i<users.length(); i++) {
						JSONObject curr_user = users.getJSONObject(i);
						if(curr_user.has("username") && curr_user.has("userID")) {
							String name = curr_user.getString("username");
							int userID = curr_user.getInt("userID");
							receivednames.add(new SearchUserItem(name, userID, this::addUser));
							System.out.println(name + " (" + userID + ")");
						}
					}
					if(i>0) {
						Main.static_add_user.populatePopup(receivednames);
						Main.static_add_user.showPopup();
					}else {
						Main.static_add_user.hidePopup();
					}
				}
				break;
			case login_fail:
				String message = "";
				if(in.has("message")) {
					message = in.getString("message");
				}
				new Login().build(server, message);
				server.isLoggedin = false;
				break;
			case contact_add_fail:
				if(in.has("message")) {
					showFail("couldn't add contact", in.getString("message"));
				}
				break;
			case room_join_fail:
				if(in.has("message")) {
					showFail("couldn't join room", in.getString("message"));
				}
				break;
			case user_unblock_fail:
				if(in.has("message")) {
					showFail("couldn't unblock user", in.getString("message"));
				}
				break;
			case contact_remove_fail:
				if(in.has("message")) {
					showFail("couldn't remove contact", in.getString("message"));
				}
				break;
			case message_fail:
				if(in.has("message")) {
					showFail("couldn't send message", in.getString("message"));
				}
				break;
			case create_group_fail:
				if(in.has("message")) {
					showFail("couldn't create group", in.getString("message"));
				}
				break;
			case room_create_fail:
				if(in.has("message")) {
					showFail("couldn't create room", in.getString("message"));
				}
				break;
			case user_create_fail:
				if(in.has("message")) {
					showFail("couldn't create user", in.getString("message"));
				}
				break;
			case group_delete_fail:
				if(in.has("message")) {
					showFail("couldn't delete group", in.getString("message"));
				}
				break;
			case room_delete_fail:
				if(in.has("message")) {
					showFail("couldn't delete room", in.getString("message"));
				}
				break;
			case user_delete_fail:
				if(in.has("message")) {
					showFail("couldn't delete user", in.getString("message"));
				}
				break;
			case group_join_fail:
				if(in.has("message")) {
					showFail("couldn't join group", in.getString("message"));
				}
				break;
			case user_change_fail:
				if(in.has("message")) {
					showFail("couldn't edit user", in.getString("message"));
				}
				break;
			case user_block_fail:
				if(in.has("message")) {
					showFail("couldn't block user", in.getString("message"));
				}
				break;
			case group_change_fail:
				if(in.has("message")) {
					showFail("couldn't aedit group", in.getString("message"));
				}
				break;
			case room_change_fail:
				if(in.has("message")) {
					showFail("couldn't edit room", in.getString("message"));
				}
				break;
			default:
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		start(this.in);
	}
	
	private void showFail(String preString, String message) {
		Alert a = new Alert(AlertType.ERROR, preString + " :" + message, ButtonType.OK);
		a.show();
	}
	
	private void addRoom(ActionEvent e) {
		if(e.getSource().getClass() == CustomMenuItem.class) {
			SearchRoomItem selected_room = (SearchRoomItem)((CustomMenuItem)e.getSource()).getContent();
			if(!selected_room.hasPW) {
				JSONObject m_join_room = new JSONObject();
				try {
					m_join_room
						.put("type", MessageType.room_join.toInt())
						.put("roomname", selected_room.roomname)
						.put("userID", server.userID)
					;
					server.send(m_join_room);
				} catch(JSONException e2) {
					e2.printStackTrace();
				}
			}else {
				new RoomInsertPW(server, selected_room.roomname);
			}
		}
	}
	
	private void addUser(ActionEvent e) {
		SearchUserItem selected_user = (SearchUserItem)((CustomMenuItem)e.getSource()).getContent();
		String selected_server = Main.static_cb_server.getSelectionModel().getSelectedItem();
		if(selected_server != null) {
			for(int i = 0; i<Main.db.servers.size(); i++) {
				Server curr = Main.db.servers.get(i);
				if(selected_server.equals(curr.Servername)) {
					JSONObject message = new JSONObject();
					try {
						message
							.put("type", MessageType.contact_add.toInt())
							.put("userID", selected_user.userID)
							.put("timestamp", Calendar.getInstance().getTimeInMillis())
						;
						curr.send(message);
					}catch(JSONException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	
	private void requestPP(int userID) {
		JSONObject m = new JSONObject();
		User curr = server.getUser(userID);
		try {
			m
				.put("userID", userID)
				.put("type", MessageType.profile_picture.toInt())
				.put("request", true)
			;
			if(curr != null) {
				System.out.println("requesting profilepicture from user \"" + curr.getDisplayName() + "\"");
			}else if(userID == server.userID) {
				System.out.println("requesting profilepicture from me");
			}
			server.send(m);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void failed(WorkerStateEvent e) {
		try {
			server.connection.close();
			in.close();
			in = null;
			server.isConnected = false;
			server.server_tab.setGraphic(new Circle(7.0, Color.RED));
			isl.running = false;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void writeMediaFragmentToFile(File f, MediaFragmented mediafragment) throws IOException{
		if(!f.exists()) {
			f.createNewFile();
		}
		FileOutputStream os = new FileOutputStream(f);
		for(int i = 0; i<mediafragment.size(); i++) {
			JSONArray curr = mediafragment.get(i);
			byte[] bytes = new byte[curr.length()];
			for(int j = 0; j<bytes.length; j++) {
				bytes[j] = new Integer(curr.getInt(j)).byteValue();
			}
			os.write(bytes);
		}
		os.close();
	}
}
