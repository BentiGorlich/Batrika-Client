package de.BentiGorlich.BatrikaClient.Network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.BentiGorlich.BatrikaBasic.MediaType;
import de.BentiGorlich.BatrikaBasic.MessageType;
import de.BentiGorlich.BatrikaClient.Basic.Server;
import javafx.concurrent.Task;

public class SendMedia extends Task<Void>{
	
	Server server;
	JSONObject messageHeader;
	File media;
	int userID;
	
	public SendMedia(Server server, JSONObject messageHeader, File media, int userID){
		this.server = server;
		this.media = media;
		this.messageHeader = messageHeader;
		this.userID = userID;
	}
	
	@Override
	protected Void call() throws Exception {
		String name = messageHeader.getString("name");
		messageHeader.put("userID", userID);
		server.send(messageHeader);
		try {
			if(!media.exists()) {
				return null;
			}
			InputStream is = new FileInputStream(media);
			byte[] buffer = new byte[13000];
			boolean completed = false;
			for(int i = 1; !completed; i++) {
				JSONObject message = new JSONObject();
				message
					.put("timestamp", Calendar.getInstance().getTimeInMillis())
					.put("request", false)
					.put("media_Nr", i)
					.put("type", MessageType.profile_picture.toInt())
					.put("media_type", MediaType.profile_picture.toInt())
					.put("name", name)
					.put("userID", userID)
				;
				JSONArray picturedata = new JSONArray();
				long readbytes = 0;
				if((readbytes = is.read(buffer)) == -1) {
					completed = true;
					break;
				}
				for(int j = 0; j<readbytes; j++) {
					picturedata.put(buffer[j]);
				}
				message.put("bytes", picturedata);
				server.send(message);
			}
			is.close();
		} catch(IOException|JSONException e) {
			e.printStackTrace();
		}
		JSONObject message = new JSONObject();
		message
			.put("type", MessageType.profile_picture.toInt())
			.put("completed", true)
			.put("name", name)
			.put("request", false)
			.put("userID", userID)
		;
		server.send(message);
		return null;
	}
	
}
