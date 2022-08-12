package net.ranzer.grimco.rafflebot.commands.admin;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;

import java.util.Date;

public class PingCommand extends BotCommand implements  Describable{

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		Date startTime = new Date();
		event.deferReply().queue(reply->{
				Date endTime = new Date();
				long lag = endTime.getTime()-startTime.getTime();
				reply.editOriginal("pong! `"+lag+"ms`").queue();
			});
		}

//	@Override
//	public void processPrefix(String[] args, MessageReceivedEvent event) {
//		Date startTime = new Date();
//		Message pong = event.getChannel().sendMessage("pong!").complete();
//		Date endTime = new Date();
//		long lag = endTime.getTime()-startTime.getTime();
//		pong.editMessage(pong.getContentDisplay()+" `"+lag+"ms`").queue();
//
//	}

	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public String getShortDescription() {
		return "pong!";
	}
	
	@Override
	public String getLongDescription() {
		return "Tests the response time of the host server";
	}


	@Override
	public Category getCategory() {
		return Category.ADMIN;
	}

	@Override
	public boolean isApplicableToPM() {
		return true;
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		return Commands.slash(getName(),getShortDescription());
	}
}
