package net.ranzer.grimco.rafflebot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public abstract class BotCommand {

	private static final String NO_PERMISSION_MESSAGE =
			"HR Says you cannot do that. if this is in error. please fill out form g-547-pm." +
					" which can be obtained by filling out form x-987584 on floor 5";

	public void runSlashCommand(SlashCommandInteractionEvent event){
		processSlash(event);
	}

	protected abstract boolean isApplicableToPM();

	protected abstract void processSlash(SlashCommandInteractionEvent event);


	public BotCommand getCommand() {
		return this;
	}

	public abstract String getName();

	public boolean hasSubcommands(){
		return false;
	}

	public List<BotCommand> getSubcommands(){
		return null;
	}

	public String getUsage() {
		return String.format("`/%s`",getName());
	}

	public abstract SlashCommandData getSlashCommandData();
}
