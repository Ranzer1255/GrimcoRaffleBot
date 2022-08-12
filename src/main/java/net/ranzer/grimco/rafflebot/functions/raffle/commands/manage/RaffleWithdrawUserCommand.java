package net.ranzer.grimco.rafflebot.functions.raffle.commands.manage;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleSubCommand;

public class RaffleWithdrawUserCommand extends AbstractRaffleSubCommand {

	private final OptionData USER = new OptionData(
			OptionType.USER,
			"user",
			"User To be removed from the raffle",
			true
	);


	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {

		Member userToRemove = event.getOption(USER.getName(), OptionMapping::getAsMember);

		raffles.get(event.getTextChannel().getId()).removeEntry(userToRemove);
		event.reply(String.format(
				MOD_WITHDRAW,
				userToRemove.getEffectiveName()
		                         )).queue();
	}

	@Override
	public String getName() {
		return "remove_user";
	}

	@Override
	public String getShortDescription() {
		return "Remove a name from the raffle";
	}

	@Override
	public String getLongDescription() {
		return getShortDescription();
	}

	@Override
	public SubcommandData getSubcommandData() {
		SubcommandData rtn = new SubcommandData(getName(),getShortDescription());

		rtn.addOptions(USER);

		return rtn;
	}
}
