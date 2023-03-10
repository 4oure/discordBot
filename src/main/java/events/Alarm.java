package events;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class Alarm implements Runnable {
	private final SlashCommandInteractionEvent event;
	private final int minutes;
	private final MessageChannel channel;

	public Alarm(SlashCommandInteractionEvent event, int minutes, MessageChannel channel) {
		this.event = event;
		this.minutes = minutes;
		this.channel = channel;

	}

	@Override
	public void run() {
		try{
			Thread.sleep(minutes * 60 * 1000);
			channel.sendMessage("Your timer is up!").queue();
		} catch(InterruptedException e){
			e.printStackTrace();
		}
	}

}
