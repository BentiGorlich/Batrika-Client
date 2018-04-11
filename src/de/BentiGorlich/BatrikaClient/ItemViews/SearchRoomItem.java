package de.BentiGorlich.BatrikaClient.ItemViews;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class SearchRoomItem extends GridPane implements SearchItem{
	
	Label r_name;
	public boolean hasPW;
	public String roomname;
	EventHandler<ActionEvent> action;
	
	public SearchRoomItem(String roomname, EventHandler<ActionEvent> action, boolean hasPW) {
		super();
		this.action = action;
		this.roomname = roomname;
		addRow(0);
		addColumn(0);
		r_name = new Label(roomname);
		add(r_name, 0,0);
	}
	
	@Override
	public EventHandler<ActionEvent> getAction() {
		return action;
	}
}
