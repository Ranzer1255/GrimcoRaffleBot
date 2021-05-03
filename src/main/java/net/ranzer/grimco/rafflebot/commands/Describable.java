package net.ranzer.grimco.rafflebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

public interface Describable {

	Category getCategory();
	
	String getName();
	List<String> getAlias();
	String getUsage(Guild g);
	String getShortDescription();
	String getLongDescription();
	Permission getPermissionRequirements();
	boolean hasSubcommands();
	List<BotCommand> getSubcommands();
}
