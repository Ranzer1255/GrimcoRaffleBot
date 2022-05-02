package net.ranzer.grimco.rafflebot.functions.draconic.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.draconic.DraconicTranslator;
import net.ranzer.grimco.rafflebot.util.StringUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class DraconicTranslateCommand extends BotCommand implements Describable{

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		if(event.getOption("reverse")!=null && event.getOption("reverse", OptionMapping::getAsBoolean)){
			event.replyEmbeds(fromDraconic(event.getOption("prase",OptionMapping::getAsString))).queue();
		} else {
			event.replyEmbeds(toDraconic(event.getOption("phrase",OptionMapping::getAsString))).queue();
		}
	}

	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {
		
		if (args[0].equals("com")){
			event.getChannel().sendMessageEmbeds(
					fromDraconic(StringUtil.arrayToString(Arrays.asList(Arrays.copyOfRange(args, 1,args.length))," ")
			)).queue();
		} else {
			event.getChannel().sendMessageEmbeds(
					toDraconic(StringUtil.arrayToString(Arrays.asList(args), " "))
			).queue();
		}
	}
	
	private MessageEmbed fromDraconic(String phrase) {
		EmbedBuilder eb = new EmbedBuilder();
		
		eb.setAuthor("Draconic Translation", "http://draconic.twilightrealm.com/", null);
		eb.setFooter("Powered by Draconic Translator from Twilight Realm", null);
		eb.setColor(new Color(0xa0760a));
		eb.addField("Draconic:", phrase, false);
		eb.addField("Common:", DraconicTranslator.translate(phrase, false), false);

		return eb.build();
	}

	private MessageEmbed toDraconic(String phrase) {
		EmbedBuilder eb = new EmbedBuilder();
		
		eb.setAuthor("Draconic Translation", "http://draconic.twilightrealm.com/", null);
		eb.setFooter("Powered by Draconic Translator from Twilight Realm", null);
		eb.setColor(new Color(0xa0760a));
		eb.addField("Common:", phrase, false);
		eb.addField("Draconic", DraconicTranslator.translate(phrase, true), false);

		return eb.build();
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("draconic","drc");
	}

	@Override
	public String getShortDescription() {
		return "I speak Draconic! What do you want to know how to say?";
	}
	
	@Override
	public String getLongDescription() {
		return "Translates a word or phrase from Common (english) to Draconic.\n\n"
				+ "`com`: will translate a word or phrase in Draconic back into Common (english)\n\n"
				+ "This Translator is powered by [Twilight Realm](http://draconic.twilightrealm.com/)";
	}

	@Override
	public String getUsage(Guild g) {
		
		return "`"+getPrefix(g)+getName()+" [com] <translation phrase>`";
		
	}
	
	@Override
	public Category getCategory() {
		return Category.MISC;
	}

	@Override
	protected boolean isApplicableToPM() {
		return true;
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		SlashCommandData rtn = Commands.slash(getName(),getShortDescription());

		rtn.addOption(OptionType.STRING,"phrase","phrase to translate",true);
		rtn.addOption(OptionType.BOOLEAN,"reverse","translate from draconic back to english",false);
		return rtn;
	}
}