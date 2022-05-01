package net.ranzer.grimco.rafflebot.functions.levels.Commands.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.functions.levels.Commands.AbstractLevelCommand;

import java.util.Collections;
import java.util.List;

public class XPThresholdCommand extends AbstractLevelCommand implements Describable {


    @Override
    public void processPrefix(String[] args, MessageReceivedEvent event) {

        try {
            if (args.length != 1) throw new IllegalArgumentException();
            int threshold = Integer.parseInt(args[0]);
            if (threshold < 0) throw new NumberFormatException();
            GuildManager.getGuildData(event.getGuild()).getRaffleData().setRaffleXPThreshold(threshold);
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
        return Collections.singletonList("threshold");
    }

    @Override
    public String getShortDescription() {
        return "the threshold for someone to enter a raffle";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription()+"\n\n" +
                "enter the amount of XP you would like a user to have before they are eligible for Raffles";
    }

    @Override
    public Permission getPermissionRequirements() {
        return Permission.ADMINISTRATOR;
    }
}
