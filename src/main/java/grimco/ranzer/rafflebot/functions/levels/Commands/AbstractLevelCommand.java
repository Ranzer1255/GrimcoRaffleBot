package grimco.ranzer.rafflebot.functions.levels.Commands;

import grimco.ranzer.rafflebot.commands.BotCommand;
import grimco.ranzer.rafflebot.commands.Category;
import grimco.ranzer.rafflebot.commands.Describable;

public abstract class AbstractLevelCommand extends BotCommand implements Describable {

    @Override
    public Category getCategory() {
        return Category.XP;
    }

    @Override
    public boolean isApplicableToPM() {
        return true;
    }
}
