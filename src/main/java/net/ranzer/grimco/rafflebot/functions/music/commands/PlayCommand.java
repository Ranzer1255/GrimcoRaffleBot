package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayer;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;
import net.ranzer.grimco.rafflebot.util.StringUtil;

import java.util.Arrays;
import java.util.List;

public class PlayCommand extends AbstractMusicSubCommand implements Describable{


	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		try {
			String songID = event.getOption(SONG.getName(), OptionMapping::getAsString);
			boolean search = event.getOption(SEARCH.getName(),true,OptionMapping::getAsBoolean);

			process(songID,event.getMember(),event.getGuild(),search);
			event.reply("now playing!").setEphemeral(true).queue();
		} catch (NoAudioChannelException e){
			event.reply(e.getMessage()).setEphemeral(true).queue();
		}
	}

	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {

		try{
			process(
					args.length>0?StringUtil.arrayToString(Arrays.asList(args), " "):null,
					event.getMember(),
					event.getGuild(),
					true
			);
		} catch (NoAudioChannelException e){
			event.getChannel().sendMessage(e.getMessage()).queue();
		}
	}

	private void process(String songID, Member member, Guild guild, boolean search) {
		if(getAudioChannel(member) == null){
			throw new NoAudioChannelException("you must be in a voice channel before i can do anything");
		}
		GuildPlayer player = GuildPlayerManager.getPlayer(guild);
		if(songID!=null){
			if (search){
				player.queueSearch(songID);
			} else {
				player.queueID(songID);
			}
		}

		if(!player.isConnected()){
			player.join(getAudioChannel(member));
		}

		player.start();
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("play","p");
	}

	@Override
	public String getShortDescription() {
		return "Start playing the first song in queue";
	}
	
	@Override
	public String getLongDescription() {
		return super.getLongDescription()+
				"this command does several things all in one.\n"
				+ "* if Caex isn't connected to a voice channel, he'll join you as per the `join` command\n\n"
				+ "* if supplied with a search string, he will search Youtube and add the song to the end of the queue as per the `queue` command\n\n"
				+ "* if caex is paused, he'll start playing again\n\n"
				+ "* last but not least, if he's stopped he'll start playing the first song in the queue";
	}
	
	@Override
	public String getUsage(Guild g) {
		return String.format("`%smusic %s [<search string>]`", getPrefix(g), getName());
	}

	@Override
	protected SubcommandData getSubCommandData() {
		SubcommandData rtn = super.getSubCommandData();
		rtn.addOptions(SONG.setRequired(false),SEARCH);
		return rtn;
	}
}
