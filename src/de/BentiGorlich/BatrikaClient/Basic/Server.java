package de.BentiGorlich.BatrikaClient.Basic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaBasic.Helper;
import de.BentiGorlich.BatrikaBasic.ImageButton;
import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaBasic.Visibility;
import de.BentiGorlich.BatrikaClient.*;
import de.BentiGorlich.BatrikaClient.Network.*;
import de.BentiGorlich.BatrikaClient.Windows.*;
import de.BentiGorlich.BatrikaClient.ItemViews.*;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Server {
	public boolean createNewUser;
	public String Servername;
	public String password;
	private String accountname;
	private String displayname;
	public String email;
	public int userID;
	public String userpassword;
	public Image profile_picture;
	public Visibility visiblility;
	
	public ArrayList<User> contacts = new ArrayList<User>();
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Integer> blockedIDs = new ArrayList<Integer>();
	public ArrayList<Room> rooms = new ArrayList<Room>();
	public ArrayList<Group> groups = new ArrayList<Group>();
	
	public int port;
	public String address;
	public Socket connection;
	public InputStreamListener isl;
	public boolean canLogin;
	public boolean isConnected = false;
	
	public ServerView serverView;
	public Tab server_tab;
	
	private int NextMessageID = 0;
	
	private DataInputStream in;
	private DataOutputStream out;
	public boolean isLoggedin = false;
	
	public ImageButton addRoom;
	public ImageButton addGroup;
	
	public User getUser(int id) {
		for(int i = 0; i<users.size(); i++) {
			User curr = users.get(i);
			if(curr.userID == id) {
				return curr;
			}
		}
		return null;
	}
	
	private void onSuccConnect(WorkerStateEvent e) {
		System.out.println("successfully connected to " + Servername);
		try {
			connection = ((Connect)e.getSource()).get();
			out = new DataOutputStream(connection.getOutputStream());
			in = new DataInputStream(connection.getInputStream());
			new ProcessInput(this, in);
			isConnected = true;
			server_tab.setGraphic(new Circle(7.0, Color.YELLOW));
			login();
		} catch(IOException e2) {
			e2.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
	}
	private void onFailConnect(WorkerStateEvent e) {
		System.out.println("couldn't connected to " + Servername);
		isConnected = false;
		isLoggedin = false;
		server_tab.setGraphic(new Circle(7.0, Color.RED));
	}
	
	public void connect() {
		Connect c = new Connect(this.address, this.port);
		c.setOnFailed(this::onFailConnect);
		c.setOnSucceeded(this::onSuccConnect);
		new Thread(c).start();
	}
	
	public void disconnect(ActionEvent e) {
		try {
			in.close();
			out.close();
			connection.close();
			isConnected = false;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void login() {
		if(canLogin) {
			if(createNewUser) {
				if(!accountname.equals("") && !displayname.equals("") && !email.equals("")) {
					createUser(email, userpassword, accountname, displayname);
				}
			}else {
				JSONObject m = new JSONObject();
				try {
					m
						.put("username", accountname)
						.put("password", userpassword)
						.put("type", MessageType.login.toInt())
					;
					send(m);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}else {
			new Login().build(this, "There are no Logininformations");
		}
	}

	public void createUser(String email, String password, String accountname, String displayname) {
		this.accountname = accountname;
		this.email = email;
		this.displayname = displayname;
		this.userpassword = password;
		this.canLogin = true;
		if(isConnected) {
			JSONObject m = new JSONObject();
			try {
				m
					.put("accountname", accountname)
					.put("displayname", displayname)
					.put("email", email)
					.put("password", password)
					.put("type", MessageType.user_create.toInt())
				;
				send(m);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void send(JSONObject m) {
		if(isConnected) {
			try {
				System.out.println("send: " + m.toString());
				out.writeUTF(m.toString());
			} catch (IOException e) {
				in = null;
				out = null;
				isConnected = false;
				server_tab.setGraphic(new Circle(7.0, Color.RED));
				e.printStackTrace();
			}
		}
	}
	
	public boolean setID(int id) {
		if(NextMessageID == 0) {
			NextMessageID = id;
			return true;
		}
		return false;
	}
	
	public void setDisplayname(String name) {
		this.displayname = name;
		if(serverView != null) {
			this.serverView.setDisplayname(name);
		}
	}
	
	public String getDisplayname() {
		return displayname;
	}
	
	public void setAccountname(String newName) {
		this.accountname = newName;
	}
	
	public String getAccountName() {
		return accountname;
	}
	public int getNextMessageID() {
		int id = NextMessageID;
		NextMessageID++;
		return id;
	}
	
	public void reconnect(ActionEvent e) {
		if(isConnected) {
			disconnect(e);
		}
		connect();
		if(isConnected) {
			login();
		}
	}
	
	private void deleteServer(ObservableValue<? extends ButtonType> val, ButtonType arg1, ButtonType arg2) {
		if(val.getValue().equals(ButtonType.YES)) {
			disconnect(new ActionEvent());
			for(int i = 0; i<contacts.size(); i++) {
				User curr = contacts.get(i);
				Main.static_all_chats.getChildren().remove(curr.uiat);
				Main.static_all_user.getChildren().remove(curr.uiati);
			}
			Main.static_server.getTabs().remove(this.server_tab);
			Main.db.servers.remove(this);
			File serverDir = new File(Main.db.ServerDirectory.getAbsolutePath() + "\\" + Servername);
			Helper.deleteDirectory(serverDir);	
		}
	}
	
	public void deleteServer(ActionEvent e) {
		Alert a = new Alert(AlertType.CONFIRMATION, 
							"This action will delete all Messages and all Media u ever received on this server. Are you sure you want to delete it anyway?",
							ButtonType.YES, ButtonType.NO);
		a.resultProperty().addListener(this::deleteServer);
		a.show();
	}
	
	public void renameServer(ActionEvent e) {
		new renameServer(this);
	}
	
	public boolean renameServer(String newName) {
		File thisserverDir = new File(Main.db.ServerDirectory + "\\" + Servername);
		File newFile = new File(Main.db.ServerDirectory + "\\" + newName);
		if(!newFile.exists()) {
			if(thisserverDir.renameTo(newFile)) {
				this.server_tab.setText(newName);
				Main.static_cb_server.getItems().remove(Servername);
				Main.static_cb_server.getItems().add(newName);
				this.Servername = newName;
				return true;
			}
		}
		return false;
	}
	
	public void addUser(User newUser) {
		newUser.chat = new UserChat(newUser, this);
		users.add(newUser);
	}
	
	public void addContact(User newContact) {
		UserItem uii = new UserItem(newContact, true);
		newContact.uii = uii;
		UserItemAllTab uiati = new UserItemAllTab(newContact, this, true);
		newContact.uiati = uiati;
		serverView.add(uii);
		Main.static_all_user.getChildren().add(uiati);
		newContact.chat = new UserChat(newContact, this);
		contacts.add(newContact);
		users.add(newContact);
		newContact.isContact = true;
	}

	public boolean hasRoom(String roomname) {
		for(int i = 0; i<rooms.size(); i++) {
			if(rooms.get(i).name.equals(roomname)) {
				return true;
			}
		}
		return false;
	}

	public Room getRoom(String roomname) {
		for(int i = 0; i<rooms.size(); i++) {
			Room curr = rooms.get(i);
			if(curr.name.equals(roomname)) {
				return curr;
			}
		}
		return null;
	}
}
