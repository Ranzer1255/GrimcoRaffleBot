package net.ranzer.grimco.rafflebot.functions.raffle.commands.run;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;

import java.util.Arrays;
import java.util.List;

public class RaffleWithdrawCommand extends AbstractRaffleCommand implements Describable {

    public static final String USER = "user";

    @Override
    protected void processSlash(SlashCommandInteractionEvent event) {
        if (event.getOption(USER)==null){//remove the calling user
            if (raffles.containsKey(event.getTextChannel().getId())){
                raffles.get(event.getTextChannel().getId()).removeEntry(event.getMember());
                event.reply(String.format(
                        USER_WITHDRAW,
                        event.getMember().getAsMention()
                )).queue();
            }
        } else {
            Member userToRemove = event.getOption(USER).getAsMember();

            //did user specify themselves? allow removal
            if(userToRemove.equals(event.getMember())){
                if (raffles.containsKey(event.getTextChannel().getId())){
                    raffles.get(event.getTextChannel().getId()).removeEntry(event.getMember());
                    event.reply(String.format(
                            USER_WITHDRAW,
                            event.getMember().getAsMention()
                    )).queue();
                }
            } else {
                //check if use is mod and allow removal
                for (Role r : event.getMember().getRoles()) {
                    if (r.getPermissions().contains(Permission.ADMINISTRATOR) || getAllowedManagementRoles(event.getGuild()).contains(r)) {
                       raffles.get(event.getTextChannel().getId()).removeEntry(userToRemove);
                       event.reply(String.format(
                               MOD_WITHDRAW,
                               userToRemove.getEffectiveName()
                       )).queue();
                       break;
                    }
                }
            }
        }
    }

    @Override
    public void processPrefix(String[] args, MessageReceivedEvent event) {
        if (args.length==0){//remove the calling user
            if (raffles.containsKey(event.getTextChannel().getId())){
                raffles.get(event.getTextChannel().getId()).removeEntry(event.getMember());
                event.getChannel().sendMessage(String.format(
                        USER_WITHDRAW,
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
                        raffles.get(event.getTextChannel().getId()).removeEntry(m);
                        event.getChannel().sendMessage(String.format(
                                MOD_WITHDRAW,
                                m.getEffectiveName()
                        )).queue();
                        break;
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

    @Override
    public SlashCommandData getSlashCommandData() {
        SlashCommandData rtn = Commands.slash(getName(),getShortDescription());

        rtn.addOption(OptionType.USER, USER,"user to remove",false);

        return rtn;
    }
}
