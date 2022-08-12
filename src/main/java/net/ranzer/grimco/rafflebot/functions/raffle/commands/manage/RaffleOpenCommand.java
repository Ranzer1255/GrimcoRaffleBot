package net.ranzer.grimco.rafflebot.functions.raffle.commands.manage;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.raffle.Raffle;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleSubCommand;

public class RaffleOpenCommand extends AbstractRaffleSubCommand implements Describable {

    public static final String RAFFLE_NAME = "name";

    @Override
    protected void processSlash(SlashCommandInteractionEvent event) {

        //Active raffle in channel. block start of a new one
        if (raffles.containsKey(event.getChannel().getId())){
            event.reply("There is already an active raffle in this channel. " +
                        "You cannot start a new one until the previous one has been Ended").setEphemeral(true).queue();
            return;
        }

        Raffle raffle;
        if (event.getOption(RAFFLE_NAME)==null){
            raffle = new Raffle("");
        } else {
            raffle = new Raffle(event.getOption(RAFFLE_NAME).getAsString());
        }

        raffles.put(event.getChannel().getId(),raffle);
        event.reply("raffle open").setEphemeral(true).queue();
        event.getChannel().sendMessageEmbeds(raffle.getEmbed())
                .setActionRows(Raffle.RAFFLE_BUTTONS_OPEN)
                .queue(raffle::setActiveMessage);

        if(!event.getJDA().getEventManager().getRegisteredListeners().contains(raffleButtonListener))
            event.getJDA().addEventListener(raffleButtonListener);
    }

    @Override
    public String getName() {
        return "start";
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
    public String getUsage() {
        return String.format(
                "`/raffle %s [<title or prize of raffle>]`",
                getName()
        );
    }

    @Override
    public SubcommandData getSubcommandData() {
        SubcommandData rtn = new SubcommandData(getName(),getShortDescription());

        rtn.addOption(OptionType.STRING, RAFFLE_NAME, "The Name or prize of your raffle", false);

        return rtn;
    }
}
