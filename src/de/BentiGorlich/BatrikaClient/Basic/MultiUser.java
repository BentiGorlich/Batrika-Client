package de.BentiGorlich.BatrikaClient.Basic;

import java.util.List;

import javafx.scene.image.Image;

public abstract class MultiUser {
	
	Image picture;
	
	
	public Image getImage() {
		return picture;
	}
	public void setImage(Image i) {
		picture = i;
	}
	
	public abstract List<User> getMembers();
}
