package grimco.ranzer.rafflebot.functions.raffle.commands;

import grimco.ranzer.rafflebot.commands.BotCommand;
import grimco.ranzer.rafflebot.commands.Category;
import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.functions.raffle.Raffle;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRaffleCommand extends BotCommand implements Describable {

    private static Map<Guild,List<Role>> allowedManagementRoles = new HashMap<>();
    protected static Map<TextChannel, Raffle> raffles = new HashMap<>();
    protected static int raffleXPThreshold = 0; //default: 0

    @Override
    public boolean isApplicableToPM() {
        return false;
    }

    @Override
    public Category getCategory() {
        return Category.RAFFLE;
    }

    /**
     * gets the
     * @param guild
     * @return
     */
    public static List<Role> getAllowedManagementRoles(Guild guild) {
        return allowedManagementRoles.get(guild);
    }
}
