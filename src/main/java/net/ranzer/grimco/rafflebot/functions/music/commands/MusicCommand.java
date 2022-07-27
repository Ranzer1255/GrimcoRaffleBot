package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.commands.admin.HelpCommand;
import net.ranzer.grimco.rafflebot.util.Logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MusicCommand extends AbstractMusicCommand implements Describable {

	public static final String JOIN = "Joining Channel %s";
	public static final String ADD = "Adding song to queue:\n%s";
	public static final String NOW_PLAYING = "Now playing:\n%s";

	private static final List<BotCommand> subCommands;

	static {
		subCommands = new ArrayList<>();
		subCommands.add(new JoinCommand());
		subCommands.add(new QueueCommand());
		subCommands.add(new InsertCommand());
		subCommands.add(new PlayCommand());
		subCommands.add(new PauseCommand());
		subCommands.add(new PlaylistCommand());
		subCommands.add(new StopCommand());
		subCommands.add(new SkipCommand());
		subCommands.add(new VolCommand());
		subCommands.add(new ShuffleCommand());
		subCommands.add(new NowPlayingCommand());
	}

	public MusicCommand() {

	}

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		Optional<BotCommand> c = subCommands.stream()
				.filter(cc -> cc.getAlias().contains(event.getSubcommandName())).findFirst();

		// Silent failure of miss-typed subcommands
		if (!c.isPresent()) {
			Logging.debug("no music subcommand");
//			channel.sendMessage(invalidUsage(event.getGuild()));
			return;
		}
		Logging.debug("Music Subclass: "+c.get().getName());
		setMusicChannel(event.getTextChannel());
		c.get().runSlashCommand(event);
	}



	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {
		if (args.length == 0) {
			event.getTextChannel().sendMessage(new MessageBuilder().setEmbeds(HelpCommand.getDescription(this)).build()).queue();
			return;
		}

		Optional<BotCommand> c = subCommands.stream().filter(cc -> cc.getAlias().contains(args[0])).findFirst();

		// Silent failure of miss-typed subcommands
		if (c.isEmpty()) {
			Logging.debug("no music subcommand");
//			channel.sendMessage(invalidUsage(event.getGuild()));
			return;
		}
		Logging.debug("Music Subclass: "+c.get().getName());
		setMusicChannel(event.getTextChannel());
		c.get().runPrefixCommand(Arrays.copyOfRange(args, 1, args.length), event);
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("music", "m");
	}

	@Override
	public String getShortDescription() {
		return "Play music!";
	}
	
	@Override
	public String getLongDescription() {
		StringBuilder sb = new StringBuilder();
		
		
		sb.append(getShortDescription()).append("\n\n");
		
		for (BotCommand cmd : subCommands) {
			sb.append(
				String.format("**%s**: %s\n", cmd.getName(), ((Describable)cmd).getShortDescription())
			);
		}
		
		return sb.toString();
	}
	
	@Override
	public String getUsage(Guild g) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("`%s%s {", getPrefix(g), getName()));
		for(BotCommand cmd : subCommands){
			sb.append(String.format("%s|", cmd.getName()));
		}
		sb.delete(sb.length()-1,sb.length());
		sb.append("}`");
				
		return sb.toString();
	}
	
	@Override
	public boolean hasSubcommands() {
		return true;
	}
	
	@Override
	public List<BotCommand> getSubcommands() {
		return subCommands;
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		SlashCommandData rtn = Commands.slash(getName(),getShortDescription());
		for(BotCommand cmd:getSubcommands()){
			if (cmd instanceof AbstractMusicSubCommand)
				rtn.addSubcommands(((AbstractMusicSubCommand) cmd).getSubCommandData());
		}
		return rtn;
	}
}
