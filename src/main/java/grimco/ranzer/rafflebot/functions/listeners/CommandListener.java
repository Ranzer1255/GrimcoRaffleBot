package grimco.ranzer.rafflebot.functions.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import grimco.ranzer.rafflebot.commands.BotCommand;
import grimco.ranzer.rafflebot.commands.admin.*;
import grimco.ranzer.rafflebot.functions.levels.Commands.XPPermCommand;
import grimco.ranzer.rafflebot.functions.raffle.commands.RaffleCommand;
import grimco.ranzer.rafflebot.functions.raffle.commands.run.RaffleEnterCommand;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
	private static CommandListener cl;
	private List<BotCommand> cmds = new ArrayList<BotCommand>();
	
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
			.addCommand(new XPPermCommand())
			.addCommand(new RaffleCommand())
			.addCommand(new RaffleEnterCommand());
	}
	
	public CommandListener addCommand(BotCommand cmd){
		this.cmds.add(cmd);
		return this;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		if (event.getAuthor().isBot()){return;}//ignore bots and self
		
		//user asked for prefix
		if (event.getMessage().isMentioned(event.getJDA().getSelfUser()) && !event.getMessage().mentionsEveryone()){
			if (containsKeyWord(event)) {
				event.getChannel().sendMessage(String.format(
						"My current prefix is: `%s`\n\n"
						+ "If you have the `administrator` permission, you may change my prefix using the `set-prefix` command.\n\n"
						+ "Do `%shelp set-prefix` for more information.",
						BotCommand.getPrefix(event.getGuild()),
						BotCommand.getPrefix(event.getGuild())
								)).queue();
			}
		}
		
		User author = event.getAuthor();
		String message = event.getMessage().getContentRaw();
		
		if(!message.toLowerCase().startsWith(BotCommand.getPrefix(event.getGuild())))
			return;
		findCommand(event, author, message); 
	}

	private boolean containsKeyWord(MessageReceivedEvent event) {
		List<String> keywords = Arrays.asList("prefix", "help", "command", "code");
		
		for (String string : keywords) {
			if(event.getMessage().getContentDisplay().contains(string))
				return true;
		}
		return false;
	}
	
	private void findCommand(MessageReceivedEvent event, User author, String message) {
		
		String[] args = message.split(" ");
		String command = args[0].toLowerCase().replace(BotCommand.getPrefix(event.getGuild()), "");
		String[] finalArgs = Arrays.copyOfRange(args, 1, args.length);
		Optional<BotCommand> c = cmds.stream().filter(cc -> cc.getAlias().contains(command)).findFirst();

		if(c.isPresent()){
			BotCommand cmd = c.get();

			callCommand(event,finalArgs,  cmd);
		}

	}

	protected void callCommand(MessageReceivedEvent event, String[] finalArgs, 
			BotCommand cmd) {
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
