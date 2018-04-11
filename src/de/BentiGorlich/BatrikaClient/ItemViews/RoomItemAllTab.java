package de.BentiGorlich.BatrikaClient.ItemViews;

import de.BentiGorlich.BatrikaClient.Basic.Room;
import de.BentiGorlich.BatrikaClient.Basic.Server;

public class RoomItemAllTab extends RoomItem{
	
	Server server;
	
	public RoomItemAllTab(Room r, Server server, boolean isInfoItem) {
		super(r, isInfoItem);
		this.server = server;
		this.topLabel.setText(topLabel.getText() + " @" + server.Servername);
	}
	
	@Override
	public void setTopText(String newName) {
		super.setTopText(newName + " @" + server.Servername);
	}

}
