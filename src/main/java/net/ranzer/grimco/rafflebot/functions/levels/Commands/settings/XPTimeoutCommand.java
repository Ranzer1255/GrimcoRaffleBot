package net.ranzer.grimco.rafflebot.functions.levels.Commands.settings;

import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.functions.levels.Commands.AbstractLevelCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;

public class XPTimeoutCommand extends AbstractLevelCommand implements Describable {


    @Override
    public void process(String[] args, MessageReceivedEvent event) {

        try{
            if (args.length!=1) throw new IllegalArgumentException();
            int interval = Integer.parseInt(args[0]);
            if (interval < 0) throw new NumberFormatException();
            GuildManager.getGuildData(event.getGuild()).setXPTimeout(interval*1000L);
            event.getChannel().sendMessage(String.format(
                    "Interval between earnings is now set to %d",
                    interval
            )).queue();
            } catch (NumberFormatException e){
                event.getChannel().sendMessage(String.format(
                        "I'm sorry but i didn't understand %s, please give me a positive whole number",
                        args[0]
                )).queue();
            } catch (IllegalArgumentException e){
                event.getChannel().sendMessage(getUsage(event.getGuild())).queue();
            }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("interval");
    }

    @Override
    public String getShortDescription() {
        return "Set the period between chances to earn XP";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription()+"\n\n" +
                "XP can only be earned once every X seconds\n" +
                "use set the interval between earnings";
    }

    @Override
    public String getUsage(Guild g) {
        return String.format("`%sxp-settings %s <time in seconds>`",
                getPrefix(g),
                getName());
    }
}