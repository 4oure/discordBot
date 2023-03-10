package events;

import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;

public class Alarm {
	public static void setTimer(int minutes) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			               public void run() {
				               System.out.println("Timer finished");
				               timer.cancel();
			               }
		               },

				minutes * 60 * 1000);
	}

	;

}
