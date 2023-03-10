package events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class discordBot extends ListenerAdapter {

	public static void main(String[] args) throws LoginException, FileNotFoundException {
		Scanner in = new Scanner(new FileReader("/Users/gavin/IdeaProjects/discordBot/src/main/java/events/token.txt"));
		StringBuilder sb = new StringBuilder();
		final String token = getToken(in, sb);
		JDA jda = create(token);


		jda.upsertCommand("slash-cmd", "This is a slash command").setGuildOnly(true).queue();
		// set this to false when ready to production
		CommandListUpdateAction commands = jda.updateCommands();
		// adds ping command
		commands
				.addCommands(Commands.slash("ping", "calculate ping of the bot")
				.setGuildOnly(true)
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)))
				.queue();
		commands
				.addCommands(Commands.slash("timer", "sets a timer for the desired minutes input").addOption(OptionType.INTEGER, "minutes", "how many minutes the timer will be set for", true))
				.queue();
	}

	@NotNull
	private static JDA create(String token) {
		return JDABuilder
				.createLight(token)
				.addEventListeners(new discordBot())
				.addEventListeners(new readyEventListener())
				.setActivity(Activity.playing("hi"))
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.build();
	}

	@NotNull
	private static String getToken(Scanner in, StringBuilder sb) {
		while(in.hasNext()){
			sb.append(in.next());
		}
		in.close();
		return sb.toString();
	}

	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
	//	event.deferReply().queue();
		switch (event.getName()) {
			case "ping" -> {
				long time = System.currentTimeMillis();
				ping(event, time);
				System.out.println("Received ping command");
			}// Ephemeral = only user can see
			case "ban" -> { // apparently it is a good idea to double-check permissions as discord can be unreliable
				if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.BAN_MEMBERS)) {
					event.reply("Insufficient Permissions")
							.setEphemeral(true)
							.queue();
				}
				Member member = (Member) event.getOption("user");
				assert member != null;
				if (!event.getMember().canInteract(member)) {
					event.reply("You can not ban this user").setEphemeral(true)
					.queue();


				}
			}
			case "timer" -> {
				int minutes = (int) Objects.requireNonNull(event.getOption("minutes")).getAsLong();
				if(minutes == 1){
					event.reply("Timer set for 1 minute!").queue();
				} else {
					event.reply("Timer set for " + minutes + " minutes!").queue();
				}
				new Thread(new Alarm(event, minutes, event.getChannel())).start();
			}
		}


	}

	private static void ping(SlashCommandInteractionEvent event, long time) {
		event.reply("Pong!")
				.setEphemeral(true)
				.flatMap(v -> event.getHook()
				.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time))
				.queue();
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		ArrayList<String> naughtyWords = new ArrayList<>();
		naughtyWords.add("Fart");
		if (event.isFromType(ChannelType.PRIVATE)) {
			System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
					event.getMessage().getContentDisplay());
		}
		else {
			System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
					event.getChannel().getName(), Objects.requireNonNull(event.getMember()).getEffectiveName(),
					event.getMessage().getContentDisplay());
		}
		for (String naughtyWord : naughtyWords){
			if (event.getMessage().getContentDisplay().equalsIgnoreCase(naughtyWord)){
				User userToBan = event.getAuthor();
				Guild guild = event.getGuild();
				try {
					guild.ban(userToBan, 0, TimeUnit.SECONDS).reason("Banned for violating chat rules.").queue();
				} catch(HierarchyException h){
					System.out.println("User is an admin or owner, permissions insufficient");
			}
			}
		}


	}
}
