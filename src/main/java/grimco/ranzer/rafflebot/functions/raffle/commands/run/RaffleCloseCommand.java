package grimco.ranzer.rafflebot.functions.raffle.commands.run;

import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

/*todo
disable current raffle

1) Close Raffle

 */
public class RaffleCloseCommand extends AbstractRaffleCommand implements Describable {
    @Override
    public void process(String[] args, MessageReceivedEvent event) {

    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("close");
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
