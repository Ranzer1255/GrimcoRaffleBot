package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

import java.util.Objects;

public abstract class AbstractMusicCommand extends BotCommand implements Describable{

	protected void setMusicChannel(TextChannel channel) {
		GuildPlayerManager.getPlayer(channel.getGuild()).getMusicListener().setMusicChannel(channel);
	}
	
	public Category getCategory(){
		return net.ranzer.grimco.rafflebot.commands.Category.MISC;
	}
	
	@Override
	public String getLongDescription() {
		return getShortDescription()+"\n\n";
	}
	
	@Override
	public String getUsage(Guild g) {
		return String.format("`%smusic %s`", getPrefix(g), getName());
	}
	
	@Override
	public boolean isApplicableToPM() {
		return false;
	}

	protected boolean notInSameAudioChannel(MessageReceivedEvent event) {

		AudioChannel requesterChannel = getAudioChannel(Objects.requireNonNull(event.getMember()));
		AudioChannel botChannel = getAudioChannel(event.getGuild().getSelfMember());

		return !Objects.equals(requesterChannel, botChannel);
	}

	protected AudioChannel getAudioChannel(Member m){
		return m.getVoiceState().getChannel();
	}
}
