package de.BentiGorlich.BatrikaClient.ItemViews;

import de.BentiGorlich.BatrikaClient.Basic.Server;
import de.BentiGorlich.BatrikaClient.Basic.User;

public class UserItemAllTab extends UserItem{
	
	Server server;
	
	public UserItemAllTab(User user, Server server, boolean isAllTabItem) {
		super(user, isAllTabItem);
		this.server = server;
		this.topLabel.setText(this.topLabel.getText() + " @" + server.Servername);
		this.nickname.textProperty().bind(user.nickname.concat(" @" + server.Servername));
	}
	
	@Override
	public void setTopText(String newName) {
		super.setTopText(newName + " @" + server.Servername);
	}
}
