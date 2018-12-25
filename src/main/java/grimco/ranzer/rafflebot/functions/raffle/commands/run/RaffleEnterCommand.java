package grimco.ranzer.rafflebot.functions.raffle.commands.run;

import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;


/*todo
users call this to enter current active raffle in this TextChannel

if no Raffle object exists for this TextChannel respond accordingly

else

check member's activity. if above threshold allow into raffle

add Member to Raffle
if member exists in Raffle
handle accordingly

 */
public class RaffleEnterCommand extends AbstractRaffleCommand implements Describable {

    // TODO: 12/24/2018 proccess enter command
    @Override
    public void process(String[] args, MessageReceivedEvent event) {

    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("enter");
    }

    @Override
    public String getShortDescription() {
        return null;// TODO: 12/24/2018 short description of enter
    }

    @Override
    public String getLongDescription() {
        return getShortDescription(); // TODO: 12/24/2018 long descripton of enter
    }
}
