package net.ranzer.grimco.rafflebot.commands.admin;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PingCommand extends BotCommand implements  Describable{

	@Override
	public void process(String[] args, MessageReceivedEvent event) {
		Date startTime = new Date();
		Message pong = event.getChannel().sendMessage("pong!").complete();
		Date endTime = new Date();
		long lag = endTime.getTime()-startTime.getTime();
		pong.editMessage(pong.getContentDisplay()+" `"+lag+"ms`").queue();
		
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("ping");
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


}
