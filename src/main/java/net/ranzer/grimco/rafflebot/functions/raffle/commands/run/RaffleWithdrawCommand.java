package net.ranzer.grimco.rafflebot.functions.raffle.commands.run;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;

import java.util.Arrays;
import java.util.List;

public class RaffleWithdrawCommand extends AbstractRaffleCommand implements Describable {


    @Override
    public void process(String[] args, MessageReceivedEvent event) {
        if (args.length==0){//remove the calling user
            if (raffles.containsKey(event.getTextChannel())){
                raffles.get(event.getTextChannel()).removeEntry(event.getMember());
                event.getChannel().sendMessage(String.format(
                        "%s, you have been removed from the raffle",
                        event.getMember().getAsMention()
                )).queue();
            } else {
                return; //silent ignore if no raffle active
            }
        }

        if (!event.getMessage().getMentionedUsers().isEmpty()){
            for (Role r: event.getMember().getRoles()){
                if (r.getPermissions().contains(Permission.ADMINISTRATOR) || getAllowedManagementRoles(event.getGuild()).contains(r)){
                    for (Member m: event.getMessage().getMentionedMembers()) {
                        raffles.get(event.getTextChannel()).removeEntry(m);
                        event.getChannel().sendMessage(String.format(
                                "%s has been removed from the raffle",
                                m.getEffectiveName()
                        )).queue();
                    }
                }
            }

        }
    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("withdraw","exit");
    }

    @Override
    public String getShortDescription() {
        return "remove your name from the raffle";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription()+"\n\n" +
                "if you have rights to run raffles, you can mention users to remove them from the raffle";
    }

    @Override
    public String getUsage(Guild g) {
        return String.format(
                "`%s%s [<@user1 @user2...]`",
                BotCommand.getPrefix(g),
                getName());
    }
}
