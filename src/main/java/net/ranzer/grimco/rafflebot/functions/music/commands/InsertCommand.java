package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayer;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

public class InsertCommand extends AbstractMusicSubCommand implements Describable {

	@Override
	public String getName() {
		return "insert";
	}

	@Override
	public void processSlash(SlashCommandInteractionEvent event) {
		event.reply("processing...").setEphemeral(true).queue();
		GuildPlayer gp = GuildPlayerManager.getPlayer(event.getGuild());

		boolean search = event.getOption(SEARCH.getName(),false,OptionMapping::getAsBoolean);
		String song = event.getOption(SONG.getName(),OptionMapping::getAsString);

		if(search){
			gp.insertSearch(song);
		} else {
			gp.insertID(song);
		}
	}

	@Override
	public String getShortDescription() {
		return "Add song to the Head of the queue";
	}
	
	@Override //todo update this
	public String getLongDescription() {
		return super.getLongDescription()
				+ "this command will search Youtube for your track and add it to the start of the queue\n\n"
				+ "to Bypass the search and insert an url or YT video/Playlist code preface the code with the guild prefix";
	}
	
	@Override
	public String getUsage() {
		
		return String.format("`/music %s <search string>`", getName());
	}

	@Override
	protected SubcommandData getSubCommandData() {
		SubcommandData rtn = super.getSubCommandData();

		rtn.addOptions(SONG,SEARCH);

		return rtn;
	}
}
