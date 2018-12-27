package grimco.ranzer.rafflebot.commands.admin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import grimco.ranzer.rafflebot.GrimcoRaffleBot;
import grimco.ranzer.rafflebot.commands.BotCommand;
import grimco.ranzer.rafflebot.commands.Category;
import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.config.BotConfiguration;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InfoCommand extends BotCommand implements Describable{

	private static final String REQUIRED_PERMISSIONS = "70372416";

	@Override
	public void process(String[] args, MessageReceivedEvent event) {

		EmbedBuilder eb = new EmbedBuilder();
		MessageBuilder mb = new MessageBuilder();
		User bot = event.getJDA().getSelfUser();
		
		if (event.getGuild()!=null) {
			eb.setColor(event.getGuild().getMember(bot).getColor());
		}
		
		if (args.length == 1&&args[0].equals("stats")) { // 1 argument !info stats
			eb = statusEmbed(event.getJDA().getSelfUser());
		} else { // !info
			eb = infoEmbed(bot);
		}

		event.getChannel().sendMessage(mb.setEmbed(eb.build()).build()).queue();

	}

	static private EmbedBuilder statusEmbed(User bot) {
		EmbedBuilder rtn = coreEmbed(bot);
		rtn.addField("Guilds", String.valueOf(bot.getJDA().getGuilds().size()), false)
		  .addField("Users", countNonBotUsers(bot.getJDA()), true)
		  .addField("Bots", countBotUsers(bot.getJDA()), true)
		  .addField("Up Time",getUpTime(), true)
		  .addField("Game", bot.getJDA().getPresence().getGame().getName(), true);
		return rtn;
	}

	static private EmbedBuilder infoEmbed(User bot) {
		EmbedBuilder rtn = coreEmbed(bot);
		  rtn.addField("Version", BotConfiguration.getInstance().getVersion(), true)
		  .addField("Language", "Java", true)
		  .addField("Artwork", "Needed", false)
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
			now=now.minusSeconds(GrimcoRaffleBot.START_TIME.until(now, ChronoUnit.SECONDS));
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

	private static String inviteLinkBuilder(User bot) {
		StringBuilder sb = new StringBuilder();

		sb.append("[No Permissions]")
		  .append("(https://discordapp.com/oauth2/authorize?client_id=").append(bot.getId()).append("&scope=bot)\n")
		  .append("[Limited Permissions]")
		  .append("(https://discordapp.com/oauth2/authorize?client_id=").append(bot.getId()).append("&scope=bot&permissions=").append(REQUIRED_PERMISSIONS).append(")\n")
		  .append("[Admin Permissions]")
		  .append("(https://discordapp.com/oauth2/authorize?client_id=").append(bot.getId()).append("&scope=bot&permissions=8)");

		return sb.toString();
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
