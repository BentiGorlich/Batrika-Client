package de.BentiGorlich.BatrikaClient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PictureButton{
	
	ImageView imv;
	EventHandler<ActionEvent> e;
	boolean over = false;
	boolean click = false;
	
	public PictureButton(ImageView imv, EventHandler<ActionEvent> e){
		this.imv = imv;
		this.e = e;
		imv.setOnMouseEntered(this::MouseOver);
		imv.setOnMouseExited(this::MouseExit);
		imv.setOnMousePressed(this::MousePressed);
		imv.setOnMouseReleased(this::MouseReleased);
	}
	
	private void MouseOver(MouseEvent e) {
		imv.setBlendMode(BlendMode.EXCLUSION);
		over = true;
	}
	private void MouseExit(MouseEvent e) {
		imv.setBlendMode(BlendMode.SRC_OVER);
		over = false;
	}
	private void MousePressed(MouseEvent e) {
		if(e.getButton().equals(MouseButton.PRIMARY)) {
			imv.setBlendMode(BlendMode.DIFFERENCE);
			click = true;
		}
	}
	private void MouseReleased(MouseEvent e) {
		if(e.getButton().equals(MouseButton.PRIMARY) && click && over) {
			imv.setBlendMode(BlendMode.EXCLUSION);
			click = false;
			this.e.handle(new ActionEvent());
		}
	}
}
