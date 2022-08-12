package net.ranzer.grimco.rafflebot.functions.draconic.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.draconic.DraconicTranslator;

import java.awt.*;

public class DraconicTranslateCommand extends BotCommand implements Describable{

	private final OptionData PHRASE = new OptionData(
			OptionType.STRING,
			"phrase",
			"phrase to translate",
			true);
	private final OptionData REVERSE = new OptionData(
			OptionType.BOOLEAN,
			"reverse",
			"translate from draconic back to english",
			false);

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {

		boolean reverse = event.getOption(REVERSE.getName(),false,OptionMapping::getAsBoolean);
		String phrase = event.getOption(PHRASE.getName(),OptionMapping::getAsString);

		if(reverse){
			event.replyEmbeds(fromDraconic(phrase)).queue();
		} else {
			event.replyEmbeds(toDraconic(phrase)).queue();
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
	public String getName() {
		return "draconic";
	}

	@Override
	public String getShortDescription() {
		return "I speak Draconic! What do you want to know how to say?";
	}
	
	@Override
	public String getLongDescription() {
		return """
				Translates a word or phrase from Common (english) to Draconic.

				`reverse`: will translate a word or phrase in Draconic back into Common (english)

				This Translator is powered by [Twilight Realm](http://draconic.twilightrealm.com/)""";
	}

	@Override
	public String getUsage() {
		return String.format("`/%s <phrase to translate> [{true|false}]",getName());
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

		rtn.addOptions(PHRASE, REVERSE);
		return rtn;
	}
}