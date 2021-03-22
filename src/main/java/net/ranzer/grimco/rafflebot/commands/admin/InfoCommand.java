package net.ranzer.grimco.rafflebot.commands.admin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.ranzer.grimco.rafflebot.GrimcoRaffleBot;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoCommand extends BotCommand implements Describable{

	@Override
	public void process(String[] args, MessageReceivedEvent event) {

		EmbedBuilder eb;
		MessageBuilder mb = new MessageBuilder();
		User bot = event.getJDA().getSelfUser();

		// 1 argument !info stats
		if (args.length == 1 && args[0].equals("stats")) {
			eb = statusEmbed(bot);
		}
		// no arguments !info
		else {
			eb = infoEmbed(bot);
		}

		//color the embed
		if (event.isFromGuild()) {
			Member m = event.getGuild().retrieveMember(bot).complete();
			eb.setColor(m.getColor());
		}

		event.getChannel().sendMessage(mb.setEmbed(eb.build()).build()).queue();

	}

	static private EmbedBuilder statusEmbed(User bot) {
		EmbedBuilder rtn = coreEmbed(bot);
		//noinspection ConstantConditions
		rtn.addField("Guilds", String.valueOf(bot.getJDA().getGuilds().size()), false)
		  .addField("Users", countNonBotUsers(bot.getJDA()), true)
		  .addField("Bots", countBotUsers(bot.getJDA()), true)
		  .addField("Up Time",getUpTime(), true)
		  .addField("Game", bot.getJDA().getPresence().getActivity().getName(), true);
		return rtn;
	}

	static private EmbedBuilder infoEmbed(User bot) {
		EmbedBuilder rtn = coreEmbed(bot);
		  rtn.addField("Version", BotConfiguration.getInstance().getVersion(), true)
		  .addField("Language", "Java", true)
		  .addField("Artwork", "[Delapouite](https://game-icons.net/1x1/delapouite/ticket.html)", false)
//		  .addField("Invite me!", inviteLinkBuilder(bot), true)
		  .addField("GitHub Repo", "[GitHub](https://github.com/Ranzer1255/GrimcoRaffleBot)\n[Bugs and Suggestions](https://github.com/Ranzer1255/GrimcoRaffleBot/issues)", true)
		  .setFooter("Please report bugs or suggestions in the link above", null);
		return rtn;
	}

	static private EmbedBuilder coreEmbed(User bot) {
		EmbedBuilder rtn = new EmbedBuilder();
		rtn.setAuthor(bot.getName(), "https://github.com/Ranzer1255/GrimcoRaffleBot", bot.getAvatarUrl())
		  .setTitle("Raffle Bot for TLoG",null)
		  .setDescription("Written by Ranzer")
		  .setThumbnail(bot.getAvatarUrl());
		return rtn;
	}

	@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
	private static String getUpTime() {
		StringBuilder sb = new StringBuilder();
		LocalDateTime now = LocalDateTime.now();
		
		if(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.YEARS)!=0){
			sb.append(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.YEARS)+" Yrs, ");
			now=now.minusYears(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.YEARS));
		}
		if(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.MONTHS)!= 0){
			sb.append(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.MONTHS)+" Mths, ");
			now=now.minusMonths(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.MONTHS));
		}
		if(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.DAYS)!=0){
			sb.append(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.DAYS)+" Days, ");
			now=now.minusDays(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.DAYS));
		}
		if(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.HOURS)!=0){
			sb.append(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.HOURS)+" Hrs, ");
			now=now.minusHours(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.HOURS));
		}
		if(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.MINUTES)!=0){
			sb.append(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.MINUTES)+" Mins, ");
			now=now.minusMinutes(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.MINUTES));
		}
		if(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.SECONDS)!=0){
			sb.append(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.SECONDS)+" Secs, ");
		}
		
		
		sb.delete(sb.length()-2, sb.length());
		sb.append(".");
		
		return sb.toString();
	}

	private static String countBotUsers(JDA api) {
		int count = 0;
		
		for(User u:api.getUsers()){
			if (u.isBot()){
				count++;
			}
		}
		
		return String.valueOf(count);
	}

	private static String countNonBotUsers(JDA api) {
		int count = 0;
		
		for(User u:api.getUsers()){
			if (!u.isBot()){
				count++;
			}
		}
		
		return String.valueOf(count);
	}

	@Override
	public String getUsage(Guild g) {
		return "`"+getPrefix(g)+getName()+" [stats]`";

	}

	@Override
	public List<String> getAlias() {

		return Arrays.asList("info", "i");
	}

	@Override
	public String getShortDescription() {

		return "Information about Caex and Author";
	}

	@Override
	public Category getCategory() {
		return Category.ADMIN;
	}

	@Override
	public String getLongDescription() {
		return    "This command gives detailed information about the bot\n\n"
				+ "`stats`: displays misc. stats reported by JDA";
	}

	@Override
	public boolean isApplicableToPM() {
		return true;
	}

}
