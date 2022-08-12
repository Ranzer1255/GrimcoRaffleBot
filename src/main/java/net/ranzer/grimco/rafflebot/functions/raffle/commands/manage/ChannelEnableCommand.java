package net.ranzer.grimco.rafflebot.functions.raffle.commands.manage;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.data.IChannelData;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleSubCommand;

public class ChannelEnableCommand extends AbstractRaffleSubCommand implements Describable {

    private static final String ENABLE = "enable";

    @Override
    protected void processSlash(SlashCommandInteractionEvent event) {
        IChannelData channel = GuildManager.getGuildData(event.getGuild()).getChannel(event.getTextChannel());
        if(event.getOption(ENABLE)!=null){
            channel.setRaffle(event.getOption(ENABLE).getAsBoolean());
        }
        event.reply(channel.getRaffle()?
                            "Raffles are Allowed in this channel":
                            "Raffles are Not Allowed in this channel"
                   ).setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "enable";
    }

    @Override
    public String getShortDescription() {
        return "toggle if raffles are allowed in this channel";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription();
    }

    @Override
    public String getUsage() {
        return String.format(
                "`/raffle %s`",
                getName()
        );
    }

    @Override
    public SubcommandData getSubcommandData() {
        SubcommandData rtn = new SubcommandData(getName(),getShortDescription());

        rtn.addOption(OptionType.BOOLEAN,ENABLE,"Enable raffles in this channel?",false);

        return rtn;
    }
}
