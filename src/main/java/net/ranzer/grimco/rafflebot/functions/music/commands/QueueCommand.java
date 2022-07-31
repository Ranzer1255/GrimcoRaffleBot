package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayer;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;
import net.ranzer.grimco.rafflebot.util.StringUtil;

import java.util.Arrays;
import java.util.List;

public class QueueCommand extends AbstractMusicSubCommand implements Describable {

	public static final int SHOW_QUEUE_LENGTH = 10;

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		GuildPlayer gp = GuildPlayerManager.getPlayer(event.getGuild());

		String songID = event.getOption(SONG.getName(), OptionMapping::getAsString);
		boolean search = event.getOption(SEARCH.getName(),true,OptionMapping::getAsBoolean);
		boolean insert = event.getOption(INSERT.getName(),false,OptionMapping::getAsBoolean);

		if(songID==null){
			event.replyEmbeds(gp.getQueueEmbed(SHOW_QUEUE_LENGTH)).queue();
		} else {
			event.reply("processing...").setEphemeral(true).queue();
			if(search) {
				if (insert){
					gp.insertSearch(songID);
				} else {
					gp.queueSearch(songID);
				}
			} else {
				if (insert){
					gp.insertID(songID);
				} else {
					gp.queueID(songID);
				}
			}
		}
	}

	@Override
	public void processPrefix(String[] args, net.dv8tion.jda.api.events.message.MessageReceivedEvent event) {
		GuildPlayer gp = GuildPlayerManager.getPlayer(event.getGuild());
		if (args.length<1) {
			event.getChannel().sendMessageEmbeds(gp.getQueueEmbed(SHOW_QUEUE_LENGTH)).queue();

		} else {
			gp.queueID(StringUtil.arrayToString(Arrays.asList(args), " "));
		}
	
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("add","queue");
	}

	@Override
	public String getShortDescription() {
		return "Add song to the play queue";
	}
	
	@Override
	public String getLongDescription() {
		return super.getLongDescription()+
				"When not given a search string, this command will list the next 10 songs currently in the queue";
	}
	
	@Override
	public String getUsage(Guild g) {
		return String.format("`%smusic %s [<search string>]`", getPrefix(g),getName());
	}

	@Override
	public SubcommandData getSubCommandData() {
		SubcommandData rtn = super.getSubCommandData();
		rtn.addOptions(SONG.setRequired(false),SEARCH,INSERT);
		return rtn;
	}
}
