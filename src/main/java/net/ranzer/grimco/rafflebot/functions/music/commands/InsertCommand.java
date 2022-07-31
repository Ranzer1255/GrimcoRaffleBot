package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayer;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;
import net.ranzer.grimco.rafflebot.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class InsertCommand extends AbstractMusicSubCommand implements Describable {

	@Override
	public List<String> getAlias() {
		return Collections.singletonList("insert");
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
	public void processPrefix(String[] args, MessageReceivedEvent event) {

		if (args[0].startsWith(getPrefix(event.getGuild()))) {
			GuildPlayerManager.getPlayer(event.getGuild()).insertSearch(StringUtil.arrayToString(args, " "));
		} else {
			GuildPlayerManager.getPlayer(event.getGuild()).insertID(args[0].substring(getPrefix(event.getGuild()).length()));
		}

	}
	@Override
	public String getShortDescription() {
		return "Add song to the Head of the queue";
	}
	
	@Override
	public String getLongDescription() {
		return super.getLongDescription()
				+ "this command will search Youtube for your track and add it to the start of the queue\n\n"
				+ "to Bypass the search and insert an url or YT video/Playlist code preface the code with the guild prefix";
	}
	
	@Override
	public String getUsage(Guild g) {
		
		return String.format("`%smusic %s <search string>`", getPrefix(g), getAlias().get(0)) + "\n"+
		       String.format("`%smusic %s %s<video or playlist code>`", getPrefix(g),getName(),getPrefix(g));
	}

	@Override
	protected SubcommandData getSubCommandData() {
		SubcommandData rtn = super.getSubCommandData();

		rtn.addOptions(SONG,SEARCH);

		return rtn;
	}
}
