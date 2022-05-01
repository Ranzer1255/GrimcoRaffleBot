package net.ranzer.grimco.rafflebot.functions.raffle.commands.run;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.raffle.Raffle;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.ranzer.grimco.rafflebot.util.StringUtil;

import java.util.Arrays;
import java.util.List;

public class RaffleOpenCommand extends AbstractRaffleCommand implements Describable {

    @Override
    public void processPrefix(String[] args, MessageReceivedEvent event) {

        //Active raffle in channel. block start of a new one
        if (raffles.containsKey(event.getChannel().getId())){
            event.getChannel().sendMessage("There is already an active raffle in this channel. " +
                    "You cannot start a new one until the previous one has been Ended").queue();
            return;
        }

        Raffle raffle;
        if (args.length==0){
            raffle = new Raffle("");
        } else {
            raffle = new Raffle(StringUtil.arrayToString(args," "));
        }

        raffles.put(event.getChannel().getId(),raffle);

        event.getChannel().sendMessageEmbeds(raffle.getEmbed())
                .setActionRows(Raffle.RAFFLE_BUTTONS_OPEN)
                .queue(raffle::setActiveMessage);

        if(!event.getJDA().getEventManager().getRegisteredListeners().contains(raffleButtonListener))
            event.getJDA().addEventListener(raffleButtonListener);
    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("open","start");
    }

    @Override
    public String getShortDescription() {
        return "Open a new raffle";
    }
    @Override
    public String getLongDescription() {
        return getShortDescription() + "\n\n" +
                "you can include an optional title or prize for your raffle by including it at the end of the command\n\n" +
                "ie: `raffle open Grand Prize` or `raffle open Game Key`";
    }

    @Override
    public Permission getPermissionRequirements() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public List<Role> getRoleRequirements(Guild guild) {
        return getAllowedManagementRoles(guild);
    }

    @Override
    public String getUsage(Guild g) {
        return String.format(
                "`%sraffle %s [<title or prize of raffle>]`",
                BotCommand.getPrefix(g),
                getName()
        );
    }
}
