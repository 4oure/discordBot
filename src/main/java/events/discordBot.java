package events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.Scanner;

public class discordBot extends ListenerAdapter {

	public static void main(String[] args) throws LoginException, FileNotFoundException {
		Scanner in = new Scanner(new FileReader("/Users/gavin/IdeaProjects/discordBot/src/main/java/events/token.txt"));
		StringBuilder sb = new StringBuilder();
		final String token = getToken(in, sb);
		JDA jda = JDABuilder.createLight(token)
				.addEventListeners(new discordBot())
			//	.addEventListeners(new readyEventListener())
				.setActivity(Activity.playing("hi"))
				.build();

		jda.upsertCommand("slash-cmd", "This is a slash command").setGuildOnly(true).queue();
		// set this to false when ready to production

		// adds ping command
		jda.updateCommands().addCommands(Commands.slash("ping", "calculate ping of the bot")
						, Commands.slash("ban", "bans a user from the server")
						.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
						.setGuildOnly(true)
						.addOption(OptionType.USER, "user", "The user to ban", true).addOption(OptionType.STRING, "reason", "The reason for ban")).queue();
		jda.updateCommands().addCommands(Commands.slash("timer", "sets a timer for the desired minutes input"));
	}

	@NotNull
	private static String getToken(Scanner in, StringBuilder sb) {
		while(in.hasNext()){
			sb.append(in.next());
		}
		in.close();
		final String token = sb.toString();
		return token;
	}

	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
	//	event.deferReply().queue();
		switch (event.getName()) {
			case "ping" -> {
				long time = System.currentTimeMillis();
				ping(event, time);
				System.out.println("Received ping command");
			}// Ephemeral = only user can see
			case "ban" -> { // apparently it is a good idea to double check permmissions as discord can be unreliable
				if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.BAN_MEMBERS)) {
					event.reply("Insufficient Permissions")
							.setEphemeral(true)
							.queue();
				}
				Member member = (Member) event.getOption("user");
				assert member != null;
				if (!event.getMember().canInteract(member)) {
					event.reply("You can not ban this user").setEphemeral(true).queue();


				}
			}
			case "timer"->{

			}
		}
		// this shows user that code works, bot is "thinking"
		//
		//


	}

	private static void ping(SlashCommandInteractionEvent event, long time) {
		event.reply("Pong!")
				.setEphemeral(true)
				.flatMap(v -> event.getHook()
						.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time))
				.queue();
	}

//	public void onMessageReceived(MessageReceivedEvent event)
//	{
//		if (event.isFromType(ChannelType.PRIVATE))
//		{
//			System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
//					event.getMessage().getContentDisplay());
//		}
//		else
//		{
//			System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
//					event.getTextChannel().getName(), event.getMember().getEffectiveName(),
//					event.getMessage().getContentDisplay());
//		}
//	}
}
