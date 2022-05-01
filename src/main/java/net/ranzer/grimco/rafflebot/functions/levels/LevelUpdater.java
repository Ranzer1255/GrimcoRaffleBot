package net.ranzer.grimco.rafflebot.functions.levels;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

//todo add xp decay
//todo change alert system to PM Member once they reach the xp threshold for Raffle participation

/*
idea for decay, rebuild database to include a table of xp history

**xp history table**
guild_id, member_id, earned, timestamp


a member's xp will be the sum of all the member's rows of this table

delete old rows according to decay time
 */

public class LevelUpdater extends ListenerAdapter{


	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.TEXT)){
			IGuildData gd = GuildManager.getGuildData(event.getGuild());

			if (isXPChannel(event.getTextChannel(), gd)) {
				if (isNotBot(event.getAuthor())) {
					if (isNotTimedOut(event.getMember(), gd)) {
						gd.getMemberData(event.getMember()).addXP(getRandomXP(gd));
					}
				}
			}

		}
	}
	
	private boolean isNotTimedOut(Member member, IGuildData gd) {
		
		return gd.getMemberData(member)==null
				||(System.currentTimeMillis()- gd.getMemberData(member).lastXP()) > gd.getXPTimeout();
	}
	
	private boolean isNotBot(User user) {
		return (user != user.getJDA().getSelfUser()) && !user.isBot();
	}
	
	private boolean isXPChannel(TextChannel channel, IGuildData gd) {
		return gd.getChannel(channel).getXPPerm();
	}
	
	private int getRandomXP(IGuildData gd) {
		
		return ThreadLocalRandom.current().nextInt(gd.getXPLowBound(), gd.getXPHighBound()+1);
	}

}
