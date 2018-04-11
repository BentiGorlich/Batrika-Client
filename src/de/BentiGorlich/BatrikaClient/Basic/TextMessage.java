package de.BentiGorlich.BatrikaClient.Basic;

import java.util.ArrayList;
import java.util.Calendar;

import de.BentiGorlich.BatrikaClient.MessageStatus;
import de.BentiGorlich.BatrikaClient.ItemViews.MessageItem;

public class TextMessage {
	public Calendar time_sent;
	public Calendar time_uploadedToServer;
	public ArrayList<CalendarUser> time_receivedByUser = new ArrayList<CalendarUser>();
	public ArrayList<CalendarUser> time_seenByUser = new ArrayList<CalendarUser>();
	public String message;
	public User sender;
	public int myMessageID;
	public int sendersMessageID;
	public boolean fromMe;
	public boolean seen = false;
	public MessageStatus status = MessageStatus.none;
	public MessageItem mi;
	
	public TextMessage(String message, Calendar timestamp, int id, User sender, boolean me) {
		this.message = message;
		this.time_sent = timestamp;
		this.myMessageID = id;
		this.sender = sender;
		this.fromMe = me;
		if(me) {
			seen = true;
		}
	}
	
	public void setSeen() {
		seen = true;
	}
	
	public long getTimeInMillis() {
		return time_sent.getTimeInMillis();
	}
	
	public String getDisplayName() {
		if(sender == null) {
			return "";
		}
		return sender.getDisplayName();
	}
}
