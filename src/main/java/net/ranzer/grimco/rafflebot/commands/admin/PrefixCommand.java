package net.ranzer.grimco.rafflebot.commands.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.data.GuildManager;

import java.util.Arrays;
import java.util.List;

public class PrefixCommand extends BotCommand implements Describable{

	@Override
	public void process(String[] args,  MessageReceivedEvent event) {
		
		switch (args.length){
		case 0:
			GuildManager.removePrefix(event.getGuild());
			event.getChannel().sendMessage(String.format("Ok boss, I'll listen for \"%s\"", BotConfiguration.getInstance().getPrefix())).queue();
			return;
		case 1:
			GuildManager.setPrefix(event.getGuild(), args[0]);
			event.getChannel().sendMessage(String.format("Ok boss, I'll listen for \"%s\"", GuildManager.getPrefix(event.getGuild()))).queue();
			return;
		default:			
			event.getChannel().sendMessage("Hey, i can't listen for more than one thing ;)").queue();
			return;
		}
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("set-prefix");
	}

	@Override
	public String getShortDescription() {
		return "Set prefix for the Guild (requires Administrator permision)";
	}

	@Override
	public String getLongDescription() {
		return    "Defines a new prefix for this guild.\n\n"
				+ "Leave the option blank to reset to the default prefix of `"+ BotConfiguration.getInstance().getPrefix()+"`";
	}
	@Override
	public String getUsage(Guild g) {
		return "`"+getPrefix(g)+getName()+" [<new prefix>]`";
	}
	
	@Override
	public Permission getPermissionRequirements() {
		
		return Permission.ADMINISTRATOR;
	}
	
	@Override
	public Category getCategory() {
		return Category.ADMIN;
	}

	@Override
	public boolean isApplicableToPM() {
		return false;
	}
}
