package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

import java.util.Arrays;
import java.util.List;

public class StopCommand extends AbstractMusicCommand implements Describable{

	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {

		if (args.length>0&&args[0].equals("keep")|| notInSameAudioChannel(event)){
			GuildPlayerManager.getPlayer(event.getGuild()).stop(false);
			return;
		}
		GuildPlayerManager.getPlayer(event.getGuild()).stop(true);
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("stop", "s");
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
	public String getUsage(Guild g) {
		return String.format("`%smusic %s [keep]`", getPrefix(g), getName());
	}
}
