package de.BentiGorlich.BatrikaClient.ItemViews;

import de.BentiGorlich.BatrikaClient.Basic.MultiUser;
import de.BentiGorlich.BatrikaClient.Basic.TextMessage;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class MultiUserMessageItem extends MessageItem{
	
	Label username = new Label();

	public MultiUserMessageItem(TextMessage textMessage, ReadOnlyDoubleProperty chatWidth, MultiUser multi) {
		super(textMessage, chatWidth);
		username.setText(textMessage.sender.getDisplayName());
		username.setTextFill(textMessage.sender.color);
		GridPane.setMargin(username, new Insets(0,10,0,10));
		main.add(username, 0, 0);
	}

}
