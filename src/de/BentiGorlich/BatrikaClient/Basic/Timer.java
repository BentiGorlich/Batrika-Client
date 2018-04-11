package de.BentiGorlich.BatrikaClient.Basic;

import javafx.concurrent.Task;

public class Timer extends Task<Void>{
	
	int time;
	
	public Timer(int millis){
		time = millis;
	}
	
	@Override
	protected Void call() throws Exception {
		Thread.sleep(time);
		return null;
	}
}
