package events;

import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;
public class Alarm {
	Timer timer = new Timer();
	public	Alarm(int minutes){
		timer.schedule(new RemindTask(), minutes*1000);
	}
}
