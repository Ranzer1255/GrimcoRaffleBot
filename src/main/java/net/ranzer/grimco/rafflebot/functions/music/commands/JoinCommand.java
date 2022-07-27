package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

import java.util.Arrays;
import java.util.List;

/**
 * Joins requester voice channel
 * 
 * @author Ranzer
 *
 */
public class JoinCommand extends AbstractMusicSubCommand implements Describable{

	@Override
	public void processSlash(SlashCommandInteractionEvent event) {
		process(event.getMember(),event.getGuild());
	}

	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {
		process(event.getMember(),event.getGuild());
	}

	private void process(Member member, Guild guild){
		AudioChannel join = getAudioChannel(member);

		if(join!=null)
			GuildPlayerManager.getPlayer(guild).join(join);
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("join","j");
	}

	@Override
	public String getShortDescription() {
		return "join bot to your current voice channel";
	}
	
	@Override
	public String getLongDescription() {
		return super.getLongDescription()
				+ "This command will join caex to whatever voice channel you are currently in";
	}
}
