package net.ranzer.grimco.rafflebot.functions.raffle.commands.manage;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleSubCommand;

import java.util.List;

/**
 * used to Bar and Unbar members from raffles
 *
 * usage raffle manage {ban | unban} [user]
 *
 * if bar. prevent user from entering raffles
 * if unbar, allow user to enter raffles
 */
public class BanUserCommand extends AbstractRaffleSubCommand implements Describable {

    public static final String USER = "user";
    public static final String MODE = "ban_or_unban";

    @Override
    protected void processSlash(SlashCommandInteractionEvent event) {
        List<Member> banned = GuildManager.getGuildData(event.getGuild()).getRaffleData().getBannedUsers();
        if(event.getOptions().isEmpty()){

            if (banned.isEmpty()){
                event.reply("No baned users").setEphemeral(true).queue();
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Member m : banned) {
                sb.append(m.getEffectiveName()).append(", ");
            }
            sb.delete(sb.length()-2,sb.length());

            event.reply("Banned users:\n" + sb).setEphemeral(true).queue();
            return;
        }

        if(event.getOption(USER)==null && event.getOption(MODE)!=null){
            event.reply("i'm sorry but you must specify a user to manage").setEphemeral(true).queue();
        }

        if(event.getOption(USER)!=null){
            Member m = event.getOption(USER,OptionMapping::getAsMember);
            if(event.getOption(MODE)==null){
                event.reply(banned.contains(m)?
                                    m.getEffectiveName()+" is currently Banned from raffles.":
                                    m.getEffectiveName()+" is not currently banned.").setEphemeral(true).queue();
            } else {
                IGuildData gd = GuildManager.getGuildData(event.getGuild());
                switch (event.getOption(MODE, OptionMapping::getAsString)) {
                    case "ban" -> {
                        gd.getMemberData(m).setBannedFromRaffle(true);
                        event.reply(m.getEffectiveName() + " is banned from future raffles.").queue();
                    }
                    case "unban" -> {
                        gd.getMemberData(m).setBannedFromRaffle(false);
                        event.reply(m.getEffectiveName() + " is unbanned from future raffles.").queue();
                    }
                    default -> event.reply("I didn't understand please use `ban` or `unban`").setEphemeral(true).queue();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "manage_users";
    }

    @Override
    public String getShortDescription() {
        return "ban and unban users from participating in raffles";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription()+"\n\n" +
                "ban: prevent user from entering raffles\n" +
                "unban: allow a banned user to enter raffles";
    }

    @Override
    public String getUsage() {
        return String.format(
                "`/raffle %s {ban | unban} <@user>`",
                getName()
        );
    }

    @Override
    public SubcommandData getSubcommandData() {
        SubcommandData rtn = new SubcommandData(getName(),getShortDescription());
        rtn.addOption(OptionType.USER, USER, "user");

        OptionData mode = new OptionData(OptionType.STRING, MODE, "ban or unban?");
        mode.addChoice("ban","ban").addChoice("unban","unban");
        rtn.addOptions(mode);

        return rtn;
    }
}
