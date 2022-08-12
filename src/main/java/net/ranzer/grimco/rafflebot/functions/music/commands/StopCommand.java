package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

public class StopCommand extends AbstractMusicSubCommand implements Describable{

	private final OptionData KEEP = new OptionData(
			OptionType.BOOLEAN,
			"keep",
			"keep the current playlist?",
			false
	);

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		boolean keep = event.getOption(KEEP.getName(), false, OptionMapping::getAsBoolean);

		event.reply("stopping...").queue();
		GuildPlayerManager.getPlayer(event.getGuild()).stop(keep);
	}

	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public String getShortDescription() {
		return "Stops the current song, leaves the VC and Clears the queue";
	}
	
	@Override
	public String getLongDescription() {
		return super.getLongDescription()+
				"`keep`: will do as above, except **__not__** clear the queue\n\n"
				+ "if called when not in Voice chat will act as if the `keep` arg was applied";
	}
	
	@Override
	public String getUsage() {
		return String.format("`/music %s [keep]`", getName());
	}

	@Override
	protected SubcommandData getSubCommandData() {
		SubcommandData rtn = super.getSubCommandData();
		rtn.addOptions(KEEP);
		return rtn;
	}
}
