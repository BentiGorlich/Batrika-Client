package de.BentiGorlich.BatrikaClient.Windows;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaBasic.ImageButton;
import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class createRoom extends BorderPane{
	
	Server s = null;
	Node backTo = null;
	
	Label roomname = new Label("Roomname");
	Label password = new Label("Password");
	Label announce = new Label();
	ImageButton pp;
	ImageButton mask;
	CheckBox show = new CheckBox("show");
	
	TextField rname = new TextField();
	TextField pw = new TextField();
	
	GridPane main = new GridPane();
	VBox content = new VBox(main);
	ScrollPane sp = new ScrollPane(content);
	
	ImageButton save;
	ImageButton back;
	
	GridPane buttons = new GridPane();
	FlowPane buttons_right = new FlowPane();
	
	public createRoom(String servername) {
		super();
		this.setCenter(sp);
		this.setTop(buttons);
		s = Main.db.getServer(servername);
		Image mask_i = new Image("file:"+Paths.get("res", "pictures","kreis_maske_mittel.png").toString());
		mask = new ImageButton(mask_i, mask_i, mask_i, this::choosePic, 0.0, 0.0);
		Image pp_i = Main.standard;
		pp = new ImageButton(pp_i, pp_i, pp_i, this::choosePic, 0.0,0.0);
		
		GridPane.setMargin(roomname, new Insets(0,10,0,10));
		GridPane.setMargin(password, new Insets(0,10,0,10));
		GridPane.setMargin(show, new Insets(10,10,30,10));
		GridPane.setMargin(rname,  new Insets(10,10,30,10));
		GridPane.setMargin(pw,  new Insets(10,10,30,10));
		
		GridPane.setColumnSpan(announce, 2);
		GridPane.setColumnSpan(roomname, 2);
		GridPane.setColumnSpan(password, 2);
		GridPane.setColumnSpan(rname, 2);
		GridPane.setColumnSpan(pp, 2);
		GridPane.setColumnSpan(mask, 2);

		AnchorPane.setBottomAnchor(this, 0.0);
		AnchorPane.setTopAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 0.0);
		AnchorPane.setRightAnchor(this, 0.0);
		
		AnchorPane.setBottomAnchor(sp, 0.0);
		AnchorPane.setTopAnchor(sp, 0.0);
		AnchorPane.setLeftAnchor(sp, 0.0);
		AnchorPane.setRightAnchor(sp, 0.0);
		
		sp.fitToWidthProperty().set(true);
		content.setAlignment(Pos.TOP_CENTER);
		announce.setText("Create Room on Server \"" + servername + "\"");
		announce.setFont(Font.font(18));
		
		buttons_right.setPrefHeight(50.0);
		buttons_right.setMaxHeight(50.0);
		buttons_right.setMinHeight(50.0);
		buttons_right.setAlignment(Pos.CENTER_RIGHT);
		buttons.setPrefHeight(50.0);
		buttons.setMaxHeight(50.0);
		buttons.setMinHeight(50.0);
		
		ColumnConstraints cb1 = new ColumnConstraints(50, 50, 50, Priority.NEVER, HPos.LEFT, false);
		ColumnConstraints cb2 = new ColumnConstraints(-1,-1,-1, Priority.ALWAYS, HPos.RIGHT, true);
		RowConstraints rb1 = new RowConstraints(50);
		
		buttons.addRow(0);
		buttons.addColumn(0);
		buttons.addColumn(1);
		
		buttons.getRowConstraints().add(0, rb1);
		buttons.getColumnConstraints().add(0, cb1);
		buttons.getColumnConstraints().add(1, cb2);
		
		Image normal = new Image("file:" + Paths.get("res", "pictures", "buttons", "back_white", "normal.png").toString());
		Image mouse_over = new Image("file:" + Paths.get("res", "pictures", "buttons", "back_white", "mouse_over.png").toString());
		Image clicked = new Image("file:" + Paths.get("res", "pictures", "buttons", "back_white", "clicked.png").toString());
		
		back = new ImageButton(normal, mouse_over, clicked, this::back, 50.0, 50.0);
		
		normal = new Image("file:" + Paths.get("res", "pictures", "buttons", "check_white", "normal.png").toString());
		mouse_over = new Image("file:" + Paths.get("res", "pictures", "buttons", "check_white", "mouse_over.png").toString());
		clicked = new Image("file:" + Paths.get("res", "pictures", "buttons", "check_white", "clicked.png").toString());
		save = new ImageButton(normal, mouse_over, clicked, this::save, 50.0, 50.0);
		
		buttons.add(back, 0, 0);
		buttons.add(buttons_right, 1, 0);
		buttons_right.getChildren().add(save);
		
		ColumnConstraints c1 = new ColumnConstraints(-1,-1,-1, Priority.ALWAYS, HPos.LEFT, true);
		ColumnConstraints c2 = new ColumnConstraints(10, 100, 120);
		RowConstraints announceRow = new RowConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, VPos.CENTER, true);
		RowConstraints pictureRow = new RowConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, VPos.CENTER, true);
		RowConstraints r2 = new RowConstraints(30.0, 30, 30);
		RowConstraints r3 = new RowConstraints(60.0, 60, 60);
		r3.setValignment(VPos.TOP);
		RowConstraints r4 = new RowConstraints(30, 30, 30);
		RowConstraints r5 = new RowConstraints(60.0, 60, 60);
		r5.setValignment(VPos.TOP);
		
		main.addRow(0);
		main.addRow(1);
		main.addRow(2);
		main.addRow(3);
		main.addRow(4);
		main.addRow(5);
		
		main.getRowConstraints().add(0, announceRow);
		main.getRowConstraints().add(1, pictureRow);
		main.getRowConstraints().add(2, r2);
		main.getRowConstraints().add(3, r3);
		main.getRowConstraints().add(4, r4);
		main.getRowConstraints().add(5, r5);
		
		main.addColumn(0);
		main.addColumn(1);
		
		main.getColumnConstraints().add(0, c1);
		main.getColumnConstraints().add(1, c2);
		
		main.add(announce, 0, 0);
		main.add(this.pp, 0, 1);
		main.add(this.mask, 0, 1);
		main.add(roomname, 0, 2);
		main.add(rname, 0, 3);
		main.add(password, 0, 4);
		main.add(pw, 0, 5);
		main.add(show, 1, 5);
		
		this.pp.fitWidthProperty().bind(this.widthProperty().divide(2.0));
		this.pp.fitHeightProperty().bind(this.widthProperty().divide(2.0));
		this.pp.setSmooth(true);
		GridPane.setHalignment(this.pp, HPos.CENTER);
		
		this.mask.setImage(new Image("file:" + Paths.get("res", "pictures", "kreis_maske_mittel.png").toString()));
		this.mask.fitWidthProperty().bind(this.widthProperty().divide(2.0));
		this.mask.fitHeightProperty().bind(this.widthProperty().divide(2.0));
		this.mask.setSmooth(true);
		GridPane.setHalignment(this.mask, HPos.CENTER);
	}
	private void choosePic(ActionEvent e) {
		try {
			FileChooser pic_file = new FileChooser();
			pic_file.setTitle("Select the ProfilePicture for your server");
			ExtensionFilter pictures = new ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.bmp");
			pic_file.getExtensionFilters().add(pictures);
			File path = pic_file.showOpenDialog(Main.mainstage);
			if(path != null) {
				Image profile = new Image(path.toURI().toURL().toString(), 2000.0, 2000.0, true, true);
				pp.setImage(profile);
				pp.clicked = profile;
				pp.mouseOver = profile;
				pp.normal = profile;
			}
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		}
	}
	
	protected void save(ActionEvent e) {
		String roomname = rname.getText();
		boolean hasPW = false;
		String pw = this.pw.getText();
		if(pw.length()>0) {
			hasPW = true;
		}
		if(roomname.length()>0 && s != null) {
			JSONObject m = new JSONObject();
			try {
				m
					.put("type", MessageType.room_create.toInt())
					.put("roomname", roomname)
					.put("hasPW", hasPW)
					.put("hasWL", false)
					.put("hasBL", false)
					.put("timestamp", Calendar.getInstance().getTimeInMillis())
					.put("password", pw)
				;
				s.send(m);
				back(e);
			}catch(JSONException e2) {
				e2.printStackTrace();
			}
		}
	}
	
	public void setBackTo(Node backTo) {
		this.backTo = backTo;
	}
	
	protected void back(ActionEvent e) {
		Main.static_content.getChildren().clear();
		if(backTo != null) {
			Main.static_content.getChildren().add(backTo);
			backTo = null;
		}else {
			Main.db.divider_pos = Main.static_main.getDividerPositions()[0];
			Main.static_main.setDividerPositions(1.0);
		}
	}
}
