package net.ranzer.grimco.rafflebot.functions.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.admin.*;
import net.ranzer.grimco.rafflebot.functions.dice.commands.DiceCommand;
import net.ranzer.grimco.rafflebot.functions.dice.commands.FateDiceCommand;
import net.ranzer.grimco.rafflebot.functions.draconic.commands.DraconicTranslateCommand;
import net.ranzer.grimco.rafflebot.functions.foldingathome.commands.FoldingAtHomeStatsCommand;
import net.ranzer.grimco.rafflebot.functions.moderation.commands.AddRoleCommand;
import net.ranzer.grimco.rafflebot.functions.moderation.commands.RemoveRoleCommand;
import net.ranzer.grimco.rafflebot.functions.moderation.commands.manage.ModRoleCommand;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.RaffleCommand;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.run.RaffleEnterCommand;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.run.RaffleWithdrawCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
	private static CommandListener cl;
	private final List<BotCommand> cmds = new ArrayList<>();
	
	public static CommandListener getInstance(){
		if (cl==null) cl = new CommandListener();
		return cl;
	}
	
	private CommandListener() {
		this.addCommand(new HelpCommand())
			.addCommand(new InfoCommand())
			.addCommand(new PingCommand())
			.addCommand(new ShutdownCommand())
			.addCommand(new PrefixCommand())
			.addCommand(new AddRoleCommand())
			.addCommand(new RemoveRoleCommand())
			.addCommand(new ModRoleCommand())
			.addCommand(new DiceCommand())
			.addCommand(new FateDiceCommand())
			.addCommand(new DraconicTranslateCommand())
//			.addCommand(new XPPermCommand())
//			.addCommand(new XPSettingsCommand())
			.addCommand(new RaffleCommand())
			.addCommand(new RaffleEnterCommand())
			.addCommand(new RaffleWithdrawCommand())
			.addCommand(new FoldingAtHomeStatsCommand());
	}
	
	private CommandListener addCommand(BotCommand cmd){
		this.cmds.add(cmd);
		return this;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		if (event.getAuthor().isBot()){return;}//ignore bots and self
		
		//user asked for prefix
		if (event.getMessage().isMentioned(event.getJDA().getSelfUser()) && !event.getMessage().mentionsEveryone()){
			if (containsKeyWord(event)) {
				//noinspection SpellCheckingInspection
				event.getChannel().sendMessage(String.format(
						"My current prefix is: `%s`\n\n"
						+ "If you have the `administrator` permission, you may change my prefix using the `set-prefix` command.\n\n"
						+ "Do `%shelp set-prefix` for more information.",
						BotCommand.getPrefix(event.getGuild()),
						BotCommand.getPrefix(event.getGuild())
								)).queue();
			}
		}

		String message = event.getMessage().getContentRaw();

		if(event.isFromGuild()){
			if(!message.toLowerCase().startsWith(BotCommand.getPrefix(event.getGuild()))) {
				return;
			}
			findCommand(event, BotCommand.getPrefix(event.getGuild()), message);
		} else {
			findCommand(event, "", message);
		}
	}

	private boolean containsKeyWord(MessageReceivedEvent event) {
		List<String> keywords = Arrays.asList("prefix", "help", "command", "code");
		
		for (String string : keywords) {
			if(event.getMessage().getContentDisplay().contains(string))
				return true;
		}
		return false;
	}
	
	private void findCommand(MessageReceivedEvent event, String prefix, String message) {

		String[] args = parseArgs(message);
		String command = args[0].toLowerCase().replace(prefix, "");
		String[] finalArgs = Arrays.copyOfRange(args, 1, args.length);
		Optional<BotCommand> c = cmds.stream().filter(cc -> cc.getAlias().contains(command)).findFirst();

		if(c.isPresent()){
			BotCommand cmd = c.get();

			callCommand(event,finalArgs,  cmd);
		}

	}

	private String[] parseArgs(String message) {
//		return message.split(" ");

		List<String> list = new ArrayList<String>();

		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(message);
		while (m.find())
			list.add(m.group(1).replace("\"",""));

		return list.toArray(new String[0]);
	}

	private void callCommand(MessageReceivedEvent event, String[] finalArgs, BotCommand cmd) {
		new Thread() {
			@Override
			public void run(){
				cmd.runCommand(finalArgs, event);
				interrupt();
			}
		}.start();
	}

	public List<BotCommand> getCommands() {
		
		return cmds;
	}

}
