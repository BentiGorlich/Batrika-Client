package de.BentiGorlich.BatrikaClient.ItemViews;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;

public class SearchUserItem extends Label implements SearchItem{
	
	public String displayname;
	public int userID;
	public EventHandler<ActionEvent> action;
	
	public SearchUserItem(String displayname, int userID, EventHandler<ActionEvent> action){
		super(displayname + " (" + String.valueOf(userID) + ")");
		this.action = action;
		this.userID = userID;
		this.displayname = displayname;
	}
	
	public EventHandler<ActionEvent> getAction(){
		return action;
	}
	
}
