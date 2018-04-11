package de.BentiGorlich.BatrikaClient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.BentiGorlich.BatrikaClient.ItemViews.SearchItem;
import de.BentiGorlich.BatrikaClient.ItemViews.SearchUserItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextField;

public class TextFieldSuggestionField extends TextField{
	/** The existing autocomplete entries. */
	private final ArrayList<SearchItem> entries;
	/** The popup used to select an entry. */
	private ContextMenu entriesPopup;

	/** Construct a new AutoCompleteTextField. */
	public TextFieldSuggestionField() {
	    super();
	    entries = new ArrayList<SearchItem>();
	    entriesPopup = new ContextMenu();
	    
	    focusedProperty().addListener(new ChangeListener<Boolean>() {
	      	@Override
	      	public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
	        	entriesPopup.hide();
	      	}
	    });
	}
	public void showPopup() {
		if (getText().length() == 0){
        	entriesPopup.hide();
        }else {
        	entriesPopup.show(TextFieldSuggestionField.this, Side.BOTTOM, 0, 0);
        }
	}
	
	public void hidePopup() {
		entriesPopup.hide();
	}
	
	/**
	* Get the existing set of autocomplete entries.
	* @return The existing autocomplete entries.
	*/
	public ArrayList<SearchItem> getEntries() { return entries; }

	/**
	* Populate the entry set with the given search results.  Display is limited to 10 entries, for performance.
	* @param searchResult The set of matching strings.
	*/
	public void populatePopup(List<SearchItem> searchResult) {
		List<CustomMenuItem> menuItems = new LinkedList<>();
	    // If you'd like more entries, modify this line.
	    int maxEntries = 10;
	    int count = Math.min(searchResult.size(), maxEntries);
	    for (int i = 0; i < count; i++){
	    	final SearchItem result = searchResult.get(i);
	    	CustomMenuItem item = new CustomMenuItem((Node) result, true);
	    	item.setOnAction(new EventHandler<ActionEvent>(){
	    		@Override
	    		public void handle(ActionEvent actionEvent) {
	    			result.getAction().handle(actionEvent);;
	    			entriesPopup.hide();
	    		}
	    	});
	    	menuItems.add(item);
	    }
	    entriesPopup.getItems().clear();
	    entriesPopup.getItems().addAll(menuItems);
	}
}
