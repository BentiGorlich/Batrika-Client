package de.BentiGorlich.BatrikaClient;

import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import de.BentiGorlich.BatrikaClient.Basic.Timer;
import de.BentiGorlich.BatrikaClient.Windows.AddServer;
import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{

	TextFieldSuggestionField add_user = new TextFieldSuggestionField();
	@FXML
	AnchorPane content;
	@FXML
	SplitPane main;
	@FXML
	TabPane server;
	@FXML
	ChoiceBox<String> cb_server;
	@FXML
	VBox allUser;
	@FXML
	VBox allChats;
	@FXML
	VBox allRooms;
	@FXML
	VBox allGroups;
	@FXML
	ProgressBar progress;
	@FXML
	Label info;
	@FXML
	GridPane grid_add_user;
	
	public static AnchorPane static_content;
	public static SplitPane static_main;
	public static TabPane static_server;
	public static ChoiceBox<String> static_cb_server;
	public static VBox static_all_user;
	public static VBox static_all_groups;
	public static VBox static_all_rooms;
	public static VBox static_all_chats;
	public static Stage mainstage;
	public static Parent root;
	public static Scene scene;
	public static DataBase db = new DataBase();
	public static Image standard;
	public static TextFieldSuggestionField static_add_user;
	
	Timer autoconnect;
	
	Stage loading_stage;
	Scene loading_s;
	Parent loading_p;
	
	@Override
	public void start(Stage s) throws Exception {
		if(!db.ClientProperties.exists()) {
			db.setupClient();
		}
		db.readClient();
		mainstage = s;
		FXMLLoader loader = new FXMLLoader(Paths.get("res", "layouts", "loading screen.fxml").toUri().toURL());
		loader.setController(this);
		loading_p = loader.load();
		loading_s = new Scene(loading_p);
		loading_stage = new Stage();
		loading_stage.setScene(loading_s);
		loading_stage.setTitle("Batrika");
		loading_stage.setX(db.x_position+db.width/2-100);
		loading_stage.setY(db.y_position+db.height/2-50);
		progress.setProgress(-1);
		info.setText("loading Messages");
		loading_stage.show();
		loadDB loading = new loadDB(db, this);
		loading.setOnSucceeded(this::loadingCompleted);
		loading.setOnFailed(this::loadingFailed);

		loader = new FXMLLoader(Paths.get("res", "layouts", "MainWindow.fxml").toUri().toURL());
		loader.setController(this);
		Main.root = loader.load();
		
		GridPane.setMargin(add_user, new Insets(10));
		grid_add_user.add(add_user, 0, 0);
		
		static_content = content;
		static_server = server;
		static_cb_server = cb_server;
		static_all_chats = allChats;
		static_all_groups = allGroups;
		static_all_rooms = allRooms;
		static_all_user = allUser;
		static_main = main;
		static_add_user = add_user;
		static_content = content;
		
		//static_content.getChildren().addListener(this::addedContent);
		
		Thread t = new Thread(loading);
		t.start();
	}
	
	public void loadingFailed(WorkerStateEvent e) {
		((loadDB)e.getSource()).getException().printStackTrace();
		loading_stage.close();
		this.exit(new WindowEvent(loading_stage, WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	public void loadingCompleted(WorkerStateEvent e) {
		loading_stage.close();
		main.setDividerPositions(db.divider_pos);
		
		scene = new Scene(root);
		
		static_cb_server.getSelectionModel().selectFirst();
		static_add_user.setPromptText("add user");
		
		mainstage.setScene(scene);
		mainstage.setWidth(db.width);
		mainstage.setHeight(db.height);;
		mainstage.setX(db.x_position);
		mainstage.setY(db.y_position);
		mainstage.setFullScreen(db.isFullscreen);
		mainstage.setMaximized(db.isMaximized);
		mainstage.setTitle("Batrika Client version 0.0");
		add_user.setOnKeyTyped(this::searchUsers);
		mainstage.setOnCloseRequest(this::exit);
		mainstage.show();
		connect(new WorkerStateEvent(autoconnect, WorkerStateEvent.WORKER_STATE_SUCCEEDED));
	}
	
	private void connect(WorkerStateEvent e) {
		for(int i = 0; i<Main.db.servers.size(); i++) {
			Server s = Main.db.servers.get(i);
			if(!s.isConnected) {
				System.out.println("Trying to connect to " + s.Servername);
				s.connect();
			}
		}
		autoconnect = new Timer(60000);
		autoconnect.setOnSucceeded(this::connect);
		new Thread(autoconnect).start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@FXML
	private void close(ActionEvent e) {
		mainstage.close();
		exit(new WindowEvent(mainstage, WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	public void exit(WindowEvent e) {
		double x = mainstage.getX();
		double y = mainstage.getY();
		double height = mainstage.getHeight();
		double width = mainstage.getWidth();
		boolean max = mainstage.isMaximized();
		boolean isFullscreen = mainstage.isFullScreen();
		
		db.x_position = x;
		db.y_position = y;
		db.width = width;
		db.height = height;
		db.isFullscreen = isFullscreen;
		db.isMaximized = max;
		db.save();
		autoconnect.cancel();
	}
	
	public void searchUsers(KeyEvent e) {
		String username = ((TextField)e.getSource()).getText() + e.getCharacter();
		username = username.replaceAll("\\p{C}", "");
		JSONObject m = new JSONObject();
		try {
			m
				.put("displayname", username)
				.put("type", MessageType.user_search.toInt())
			;
			String servername = static_cb_server.getSelectionModel().getSelectedItem();
			Server selected = Main.db.getServer(servername);
			if(selected != null) {
				selected.send(m);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(username.equals("")) {
			Main.static_add_user.hidePopup();
		}
	}
	
	@FXML
	private void add_server(ActionEvent e) {
		new AddServer().build();
	}
}
