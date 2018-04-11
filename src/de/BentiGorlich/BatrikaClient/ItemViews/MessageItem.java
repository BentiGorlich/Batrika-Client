package de.BentiGorlich.BatrikaClient.ItemViews;

import java.util.Calendar;

import de.BentiGorlich.BatrikaBasic.Helper;
import de.BentiGorlich.BatrikaClient.Main;
import de.BentiGorlich.BatrikaClient.Basic.TextMessage;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class MessageItem extends HBox{

	public Text message;
	public GridPane main;
	private Label status;
	public TextMessage textMessage;
	
	public boolean clicked = false;
	public boolean mouseOver = false;
	public SimpleBooleanProperty selected = new SimpleBooleanProperty(false);
	
	
	public MessageItem(TextMessage textMessage, ReadOnlyDoubleProperty chatWidth) {
		this.textMessage = textMessage;
		this.textMessage.mi = this;
		message = new Text(textMessage.message);
		message.setTextAlignment(TextAlignment.JUSTIFY);
		HBox.setHgrow(message, Priority.ALWAYS);
		
		HBox t = new HBox();
		t.setMaxWidth(Main.static_content.widthProperty().doubleValue());
		t.getChildren().add(message);
		t.setAlignment(Pos.CENTER);
		
		Calendar m_time = Calendar.getInstance();
		m_time.setTimeInMillis(textMessage.getTimeInMillis());
		Label time = new Label(Helper.getTime(m_time) + " | " + textMessage.myMessageID);
		time.setFont(Font.font(8));
		
		GridPane.setVgrow(t, Priority.ALWAYS);
		
		status = new Label();
		status.setFont(Font.font(8));
		editStatus();
		
		GridPane.setColumnSpan(message, 2);
		
		GridPane.setMargin(time, new Insets(0, 10, 0, 10));
		GridPane.setMargin(message, new Insets(0, 10, 0, 10));
		GridPane.setMargin(status, new Insets(0,10,0,0));
		
		main = new GridPane();
		main.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1.0))));
		main.addRow(0);
		main.getRowConstraints().add(0, new RowConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, VPos.TOP, true));
		if(textMessage.fromMe) {
			main.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(10), new Insets(0))));
			message.setTextAlignment(TextAlignment.RIGHT);
			setAlignment(Pos.CENTER_RIGHT);
			main.addRow(1);
			main.getRowConstraints().add(1, new RowConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, VPos.BOTTOM, true));
			main.addRow(2);
			main.getRowConstraints().add(2, new RowConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, VPos.TOP, true));
			main.addColumn(0);
			main.getColumnConstraints().add(0, new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.SOMETIMES, HPos.LEFT, true));
			main.addColumn(1);
			main.getColumnConstraints().add(1, new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.NEVER, HPos.CENTER, false));
			
			main.add(message, 0, 1);
			main.add(time, 0, 2);
			main.add(status, 1, 2);
		}else {
			main.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(0))));
			message.setTextAlignment(TextAlignment.LEFT);
			setAlignment(Pos.CENTER_LEFT);
			main.addRow(1);
			main.getRowConstraints().add(1, new RowConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, VPos.BOTTOM, true));
			main.addRow(2);
			main.getRowConstraints().add(2, new RowConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.NEVER, VPos.TOP, false));
			main.addColumn(0);
			main.getColumnConstraints().add(0, new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, HPos.LEFT, true));
			
			main.add(message, 0, 1);
			main.add(time, 0, 2);
		}
		
		VBox.setMargin(this, new Insets(10,10,0,10));
		
		getChildren().add(main);
		setMaxHeight(USE_COMPUTED_SIZE);
		setMinHeight(USE_COMPUTED_SIZE);
		setMaxHeight(USE_COMPUTED_SIZE);
		
		setOnMouseEntered(this::mouseEntered);
		setOnMouseExited(this::mouseExited);
		setOnMousePressed(this::mouseClicked);
		setOnMouseReleased(this::mouseReleased);
		
		chatWidth.addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> val, Number oldValue, Number newValue) {
				computeWrap();
			}
		});
		
	}
	
	public void select() {
		selected.set(true);
		this.setBackground(new Background(new BackgroundFill(Color.AQUAMARINE, new CornerRadii(0), new Insets(0))));
	}
	
	public void unselect() {
		selected.set(false);
		this.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(0), new Insets(0))));
	}
	
	private void mouseEntered(MouseEvent e) {
		mouseOver = true;
		if(!selected.get()) {
			this.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(0), new Insets(0))));
		}
	}
	
	private void mouseExited(MouseEvent e) {
		mouseOver = false;
		if(!selected.get()) {
			this.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(0), new Insets(0))));
		}
	}
	
	private void mouseClicked(MouseEvent e) {
		if(e.getButton().equals(MouseButton.PRIMARY) && mouseOver) {
			clicked = true;
		}
	}
	
	private void mouseReleased(MouseEvent e) {
		if(e.getButton().equals(MouseButton.PRIMARY) && clicked && mouseOver) {
			clicked = false;
			if(selected.get()) {
				unselect();
				this.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(0), new Insets(0))));
			}else {
				select();
			}
		}
	}
	
	public void editStatus() {
		switch(textMessage.status) {
			default: status.setText("---"); break;
			case received_by_server: status.setText("^"); break;
			case received_by_user: status.setText("<"); break;
			case seen_by_client: status.setText("(°-°)"); break;
		}
	}
	
	public void computeWrap() {
		Double chatWidth = Main.static_content.widthProperty().doubleValue();
		Double messageWidth = message.getFont().getSize()*message.getText().length();
		if(messageWidth>chatWidth*0.6) {
			message.setWrappingWidth(chatWidth*0.5);
		}else {
			message.setWrappingWidth(0);
		}
	}
	
}
