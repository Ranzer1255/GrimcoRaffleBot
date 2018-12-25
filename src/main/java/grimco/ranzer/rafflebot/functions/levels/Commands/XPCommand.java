package grimco.ranzer.rafflebot.functions.levels.Commands;

import grimco.ranzer.rafflebot.commands.BotCommand;
import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.commands.admin.HelpCommand;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XPCommand extends AbstractLevelCommand implements Describable {

    static List<BotCommand> subCommands = new ArrayList<>();

    static{
        //todo add subCommands here
    }

    @Override
    public void process(String[] args, MessageReceivedEvent event) {

        if (args.length == 0) {
            event.getTextChannel().sendMessage(new MessageBuilder().setEmbed(HelpCommand.getDescription(this, event.getGuild())).build()).queue();
            return;
        }

      subCommands.stream()
              .filter(cc -> cc.getAlias().contains(args[0]))
              .findFirst()
              .ifPresent(
                c -> c.runCommand(Arrays.copyOfRange(args, 1, args.length), event)
              );


    }

    @Override
    public List<String> getAlias() {
        return null;
    }

    @Override
    public String getShortDescription() {
        return null;
    }

    @Override
    public String getLongDescription() {
        return null;
    }
}
