package grimco.ranzer.rafflebot.functions.levels.Commands.settings;

import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.data.IChannelData;
import grimco.ranzer.rafflebot.data.GuildManager;
import grimco.ranzer.rafflebot.functions.levels.Commands.AbstractLevelCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class XPPermCommand extends AbstractLevelCommand implements Describable{

	@Override
	public void process(String[] args,  MessageReceivedEvent event) {
		// TODO: 12/24/2018 rewrite this as a switch statement.. it should look cleaner that way

		if (args.length==0){
			event.getChannel().sendMessage("This channel's XP setting is currently: "+
					GuildManager.getGuildData(event.getGuild()).getChannel(event.getTextChannel()).getXPPerm()).queue();
			return;
		}
		if (args.length!=1||!(args[0].toLowerCase().equals("true")||args[0].toLowerCase().equals("false"))){
			event.getChannel().sendMessage("I'm sorry i didn't understand that please follow the usage\n"
								+getUsage(event.getGuild())).queue();
			return;
		}
		
		GuildManager.getGuildData(event.getGuild()).getChannel(event.getTextChannel()).setXPPerm(Boolean.parseBoolean(args[0]));
		
		if(Boolean.parseBoolean(args[0])){
			event.getChannel().sendMessage("You will now earn XP in this channel").queue();
		} else {
			event.getChannel().sendMessage("you will now __not__ earn XP in this channel").queue();
		}
		
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("earn-xp");
	}

	@Override
	public String getUsage(Guild g) {
		return "`"+getPrefix(g)+getName()+" [{true|false}]`";
	}

	@Override
	public String getShortDescription() {
		return "Set xp earnings for current text channel";
	}

	@Override
	public String getLongDescription() {
		return "This command sets the xp earning permmision for the channel within Caex\n"
				+ "leaving the value blank will return the current setting for this Channel\n\n"
				+ "`True`: users will earn xp in this channel\n"
				+ "`False`: users will not earn XP\n"
				+ "`Default Value`: " + IChannelData.DEFAULT_XP_SETTING;
	}

	@Override
	public Permission getPermissionRequirements() {
		
		return Permission.ADMINISTRATOR;
	}
}
