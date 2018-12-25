package grimco.ranzer.rafflebot.functions.levels;

import java.util.concurrent.ThreadLocalRandom;

import grimco.ranzer.rafflebot.data.GuildManager;
import grimco.ranzer.rafflebot.data.IGuildData;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

//TODO change the XP eanrings to be adjustable
//todo add xp decay
//todo change alert system to PM Member once they reach the xp threshold for Raffle participation

/*
idea, rebuild database to include a table of xp history

**xp history table**
guild_id, member_id, earned, timestamp


a member's xp will be the sum of all the member's rows of this table

delete old rows according to decay time
 */

public class LevelUpdater extends ListenerAdapter{

	private static final long MESSAGE_TIMEOUT = 60000L;
	private final static int XP_LOWBOUND = 15, XP_HIGHBOUND = 25;
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		IGuildData gd = GuildManager.getGuildData(event.getGuild());

		if (isXPChannel(event, gd)) {
			if (isNotBot(event)) {
				if (isNotTimedOut(event, gd)) {
					gd.addXP(event.getAuthor(), getRandomXP(),event.getChannel());

				}
			} 
		}
		
	}
	
	private boolean isNotTimedOut(GuildMessageReceivedEvent event, IGuildData gd) {
		
		return gd.getUserLevel(event.getMember())==null
				||(System.currentTimeMillis()- gd.getUserLevel(event.getMember()).getLastXPTime()) > MESSAGE_TIMEOUT;
	}
	
	private boolean isNotBot(GuildMessageReceivedEvent event) {
		return (event.getAuthor() != event.getJDA().getSelfUser()) && !event.getAuthor().isBot();
	}
	
	private boolean isXPChannel(GuildMessageReceivedEvent event, IGuildData gd) {
		return gd.getChannel(event.getChannel()).getXPPerm();
	}
	
	private int getRandomXP() {
		
		return ThreadLocalRandom.current().nextInt(XP_LOWBOUND, XP_HIGHBOUND+1);
	}

}
