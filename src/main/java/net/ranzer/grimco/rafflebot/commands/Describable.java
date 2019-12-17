package net.ranzer.grimco.rafflebot.commands;

import java.util.List;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

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
