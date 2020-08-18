package net.ranzer.grimco.rafflebot.functions.levels.Commands.settings;

import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import net.ranzer.grimco.rafflebot.functions.levels.Commands.AbstractLevelCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;

public class XPEarningCommand extends AbstractLevelCommand implements Describable {
    @Override
    public void process(String[] args, MessageReceivedEvent event) {

        IGuildData gd = GuildManager.getGuildData(event.getGuild());
        try {

            if (args.length!=2) throw new IllegalArgumentException(getUsage(event.getGuild()));

            int low = Integer.parseInt(args[0]);
            int high = Integer.parseInt(args[1]);
            if (low<0||high<0||low>high) throw new IllegalArgumentException("low and high must be positive whole numbers," +
                    " and high must be greater than or equal to low");

            gd.setXPBounds(low, high);
            event.getChannel().sendMessage(String.format(
                    "you will now earn xp between %d and %d",
                    low,
                    high
            )).queue();

        } catch (NumberFormatException e){

            event.getChannel().sendMessage(String.format(
                    "I'm sorry but i didn't understand %s, please give me a positive whole number",
                    args[0]+" or "+args[1]
            )).queue();

        } catch (IllegalArgumentException e){

            event.getChannel().sendMessage(e.getMessage()).queue();

        }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("bounds");
    }

    @Override
    public String getShortDescription() {
        return "Set the bounds for xp earning";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription()+"\n\n" +
                "the bot will give a random amount of XP each message between the\n" +
                "low bound and high bound, inclusive\n" +
                "low and high must be positive whole numbers\n" +
                "to set to a determined amount set both bounds equal to each other";
    }

    @Override
    public String getUsage(Guild g) {
        return String.format("%sxp-settings %s <low> <high>",
                getPrefix(g),
                getName());
    }

    @Override
    public Permission getPermissionRequirements() {
        return Permission.ADMINISTRATOR;
    }
}
