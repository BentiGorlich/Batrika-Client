package de.BentiGorlich.BatrikaClient.ItemViews;

import java.net.MalformedURLException;
import java.nio.file.Paths;

import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.TextMessage;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

public abstract class Item extends HBox{
	
	Image mask_normal;
	Image mask_mouse_over;
	Image mask_clicked;
	ImageView mask;
	public ImageView picture;
	GridPane main;
	Label topLabel;
	Label bottomRightLabel;
	Label bottomLeftLabel;
	
	ProgressIndicator progress = new ProgressIndicator();
	double progressMinValue = 0.0;
	double progressMaxValue = 1.0;
	
	public boolean mouse = false;
	public boolean click = false;
	
	Double mouseX;
	Double mouseY;
	
	ContextMenu context;
	
	public TextMessage LastMessage = null;
	
	Item(){
		super();
		main = new GridPane();
		main.addRow(0);
		main.getRowConstraints().add(new RowConstraints(25.0, 25.0, 25.0, Priority.NEVER, VPos.CENTER, false));
		main.addRow(1);
		main.getRowConstraints().add(new RowConstraints(25.0, 25.0, 25.0, Priority.NEVER, VPos.TOP, false));
		main.addColumn(0);
		main.getColumnConstraints().add(new ColumnConstraints(50, 50, 50, Priority.NEVER, HPos.CENTER, false));
		main.addColumn(1);
		ColumnConstraints c2 = new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, HPos.LEFT, true);
		c2.prefWidthProperty().bind(this.widthProperty().subtract(100));
		main.getColumnConstraints().add(c2);
		main.addColumn(2);
		main.getColumnConstraints().add(new ColumnConstraints(50, 50, 50, Priority.NEVER, HPos.CENTER, false));
		
		picture = new ImageView(Main.standard);
		picture.setSmooth(true);
		picture.setFitHeight(50.0);
		picture.setFitWidth(50.0);
		picture.setPreserveRatio(true);
		GridPane.setRowSpan(picture, 2);
		main.add(picture, 0, 0);
		
		try {
			mask_normal = new Image(Paths.get("res", "pictures", "buttons", "mask", "normal.png").toUri().toURL().toString());
			mask_mouse_over = new Image(Paths.get("res", "pictures", "buttons", "mask", "mouse_over.png").toUri().toURL().toString());
			mask_clicked = new Image(Paths.get("res", "pictures", "buttons", "mask", "clicked.png").toUri().toURL().toString());
			mask = new ImageView(mask_normal);
			mask.setFitHeight(50.0);
			mask.setFitWidth(50.0);
			mask.setPreserveRatio(true);
			mask.setSmooth(true);
			GridPane.setRowSpan(mask, 2);
			main.add(mask, 0, 0);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		progress.setVisible(false);
		progress.setTooltip(new Tooltip("downloading new profilepicture"));
		progress.setStyle("-fx-fill:null;");
		GridPane.setRowSpan(progress, 2);
		main.add(progress, 0, 0);

		bottomRightLabel = new Label();
		bottomRightLabel.setTextFill(Color.WHITE);
		HBox wrap = new HBox();
		wrap.setVisible(false);
		wrap.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(10), new Insets(0))));
		wrap.setAlignment(Pos.CENTER);
		wrap.getChildren().add(bottomRightLabel);
		HBox.setMargin(bottomRightLabel, new Insets(0, 5, 0, 5));
		main.add(wrap, 2, 1);
		
		bottomLeftLabel = new Label();
		bottomLeftLabel.setTextOverrun(OverrunStyle.CLIP);
		bottomLeftLabel.setFont(Font.font(Font.getDefault().getName(), FontPosture.ITALIC, 10));
		main.add(bottomLeftLabel, 1, 1);
		
		context = new ContextMenu();
		context.autoHideProperty().set(true);
		this.populateContextMenu();
		
		setOnMouseMoved(this::mouseMoved);
		setMaxWidth(USE_COMPUTED_SIZE);
		setHgrow(this, Priority.ALWAYS);
		setPrefHeight(50.0);
		getChildren().add(main);
		setOnMousePressed(this::mousePressed);
		setOnMouseReleased(this::mouseReleased);
		setOnMouseEntered(this::mouseEntered);
		setOnMouseExited(this::mouseExited);
		
		setOnContextMenuRequested(this::contextRequested);
		
		topLabel = new Label();
		main.add(topLabel, 1, 0);
	}
	
	public void setTopText(String newName) {
		topLabel.setText(newName);
	}
	
	private void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
	public void setLoadPP() {
		progress.setProgress(-1);
		progress.setVisible(true);
	}
	
	public void setMinMax(double minValue, double maxValue) {
		this.progressMinValue = minValue;
		this.progressMaxValue = maxValue;
	}
	
	protected abstract void populateContextMenu();
	public abstract void mouseReleased(MouseEvent e);
	
	public void setProgress(double value){
		if(value > progressMaxValue) {
			this.progress.setProgress(1.0);
			return;
		}
		if(value < progressMinValue) {
			this.progress.setProgress(0.0);
			return;
		}
		value -= progressMinValue;
		double progress = 100 * value / (progressMaxValue-progressMinValue);
		this.progress.setProgress(progress);
	}
	
	public void setProgressCompleted() {
		progress.setVisible(false);
	}

	private void contextRequested(ContextMenuEvent e) {
		context.show(this, Side.BOTTOM, mouseX, -(this.getHeight()-mouseY));
	}
	
	private void mousePressed(MouseEvent e) {
		if(e.getButton().equals(MouseButton.PRIMARY)) {
			setBackground(new Background(new BackgroundFill(Color.AQUAMARINE, CornerRadii.EMPTY, new Insets(0))));
			mask.setImage(mask_clicked);
			click = true;
		}
	}
	
	private void mouseEntered(MouseEvent e) {
		setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, new Insets(0))));
		mask.setImage(mask_mouse_over);
		mouse = true;
	}
	
	private void mouseExited(MouseEvent e) {
		setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, new Insets(0))));
		mask.setImage(mask_normal);
		mouse = false;
	}
	
	public void setImage(Image i) {
		picture.setImage(i);
	}
	public Image getImage() {
		return picture.getImage();
	}
	public Double getProgress() {
		return this.progress.getProgress();
	}
	
	public String getTopText() {
		return topLabel.getText();
	}
	
	public long getTimeFromLastMessage() {
		return LastMessage.getTimeInMillis();
	}
}
