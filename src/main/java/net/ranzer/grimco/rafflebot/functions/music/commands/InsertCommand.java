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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class InsertCommand extends AbstractMusicSubCommand implements Describable {

	private static final String SCO_VIDEO = "video";
	private static final String SCO_ID_SEARCH = "knowid";

	@Override
	public List<String> getAlias() {
		return Collections.singletonList("insert");
	}

	@Override
	public void processSlash(SlashCommandInteractionEvent event) {

		event.deferReply().queue();
		GuildPlayer gp = GuildPlayerManager.getPlayer(event.getGuild());
		OptionMapping searchOption = event.getOption(SCO_ID_SEARCH);
		if(searchOption ==null || searchOption.getAsBoolean()){
			gp.insertID(Objects.requireNonNull(event.getOption(SCO_VIDEO)).getAsString());
		} else {
//			gp.insertSearch(Objects.requireNonNull(event.getOption(SCO_VIDEO)).getAsString());
			event.reply("searching is curreing disabled please supply the URL or the video ID").setEphemeral(true).queue();
		}
	}

	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {

//		if (args[0].startsWith(getPrefix(event.getGuild()))) {
			GuildPlayerManager.getPlayer(event.getGuild()).insertID(args[0].substring(getPrefix(event.getGuild()).length()));
//		} else {
//			GuildPlayerManager.getPlayer(event.getGuild()).insertSearch(StringUtil.arrayToString(args, " "));
//		}

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
		SubcommandData rtn = new SubcommandData(getName(),getShortDescription());

		rtn.addOption(OptionType.STRING,SCO_VIDEO, "what video to search for",true );
		rtn.addOption(OptionType.BOOLEAN, SCO_ID_SEARCH,"if you have the video ID say \"true\" to skip the search",false);
		return rtn;
	}
}
