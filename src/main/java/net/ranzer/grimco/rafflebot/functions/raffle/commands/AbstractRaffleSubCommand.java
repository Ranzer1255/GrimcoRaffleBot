package net.ranzer.grimco.rafflebot.functions.raffle.commands;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class AbstractRaffleSubCommand extends AbstractRaffleCommand{


	public abstract SubcommandData getSubcommandData();

	@Override
	public final SlashCommandData getSlashCommandData() {
		return null;
	}
}
