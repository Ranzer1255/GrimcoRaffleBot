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
    public void process(String[] args, MessageReceivedEvent event) {
        Raffle raffle;
        if (args.length==0){
            raffle = new Raffle("");
        } else {
            raffle = new Raffle(StringUtil.arrayToString(args," "));
        }

        raffles.put(event.getTextChannel().getId(),raffle);

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
        return getShortDescription();
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
                "`%sraffle %s`",
                BotCommand.getPrefix(g),
                getName()
        );
    }
}
