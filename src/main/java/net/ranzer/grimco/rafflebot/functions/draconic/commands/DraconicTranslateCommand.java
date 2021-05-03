package net.ranzer.grimco.rafflebot.functions.draconic.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
	public void process(String[] args,  MessageReceivedEvent event) {
		
		if (args[0].equals("com")){
			fromDraconic(StringUtil.arrayToString(Arrays.asList(Arrays.copyOfRange(args, 1,args.length))," "), event);
			return;
		}
		
		toDraconic(StringUtil.arrayToString(Arrays.asList(args)," "), event);
	}
	
	private void fromDraconic(String phrase, MessageReceivedEvent event) {
		EmbedBuilder eb = new EmbedBuilder();
		MessageBuilder mb = new MessageBuilder();
		
		eb.setAuthor("Draconic Translation", "http://draconic.twilightrealm.com/", null);
		eb.setFooter("Powered by Draconic Translator from Twilight Realm", null);
		eb.setColor(new Color(0xa0760a));
		eb.addField("Draconic:", phrase, false);
		eb.addField("Common:", DraconicTranslator.translate(phrase, false), false);
		
		event.getChannel().sendMessage(mb.setEmbed(eb.build()).build()).queue();
	}

	private void toDraconic(String phrase, MessageReceivedEvent event) {
		EmbedBuilder eb = new EmbedBuilder();
		MessageBuilder mb = new MessageBuilder();
		
		eb.setAuthor("Draconic Translation", "http://draconic.twilightrealm.com/", null);
		eb.setFooter("Powered by Draconic Translator from Twilight Realm", null);
		eb.setColor(new Color(0xa0760a));
		eb.addField("Common:", phrase, false);
		eb.addField("Draconic", DraconicTranslator.translate(phrase, true), false);
		
		event.getChannel().sendMessage(mb.setEmbed(eb.build()).build()).queue();
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
}