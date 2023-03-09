package events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class readyEventListener implements EventListener {
	public static void main(String[] args) {

	}

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof ReadyEvent){
			System.out.println("The bot is online.");
		}

		//if (event instanceof MessageReceivedEvent)

	}
}
