package net.ranzer.grimco.rafflebot.functions.levels.Commands;

import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;

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
