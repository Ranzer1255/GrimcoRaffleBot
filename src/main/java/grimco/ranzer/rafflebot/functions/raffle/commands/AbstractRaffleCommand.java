package grimco.ranzer.rafflebot.functions.raffle.commands;

import grimco.ranzer.rafflebot.commands.BotCommand;
import grimco.ranzer.rafflebot.commands.Category;
import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.data.GuildManager;
import grimco.ranzer.rafflebot.data.IRaffleData;
import grimco.ranzer.rafflebot.functions.raffle.Raffle;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRaffleCommand extends BotCommand implements Describable {

    // TODO: 12/26/2018 fix usage text to show the parrent command in the subcommands
    protected static Map<TextChannel, Raffle> raffles = new HashMap<>();

    @Override
    public void process(String[] args, MessageReceivedEvent event) {
        boolean raffle = GuildManager.getGuildData(event.getGuild()).getChannel(event.getTextChannel()).getRaffle();
        if (!raffle){
            event.getChannel().sendMessage(
                    "Raffles are currently disabled in this channel"
            ).queue();
        }
    }

    @Override
    public boolean isApplicableToPM() {
        return false;
    }

    @Override
    public Category getCategory() {
        return Category.RAFFLE;
    }

    /**
     * gets the Roles that can manage Raffles in the supplied Guild
     * @param guild guild for which these roles apply
     * @return a List<Role> of all the Roles allowed to manage raffles
     */
    protected static List<Role> getAllowedManagementRoles(Guild guild) {
       return GuildManager.getGuildData(guild).getRaffleData().allowedManagementRoles();
    }

    protected IRaffleData getRaffleData(Guild g){
        return GuildManager.getGuildData(g).getRaffleData();
    }
}
