package net.ranzer.grimco.rafflebot.functions.raffle.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.ranzer.grimco.rafflebot.GrimcoRaffleBot;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.data.IRaffleData;
import net.ranzer.grimco.rafflebot.functions.raffle.Raffle;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRaffleCommand extends BotCommand implements Describable {

    public static final String BARRED_MESSAGE = "sorry %s, but you have been barred from entry";
    public static final String INACTIVE_MESSAGE = "Sorry %s, but you haven't been active enough in the community to be eligible for raffles";
    public static final String RAFFLE_EXISTS_MESSAGE = "%s, You've already entered. call %swithdraw if you would like to be removed.";
    public static final String RAFFLE_CLOSED_MESSAGE = "sorry %s, but the raffle's been closed and a drawing is about to happen.";
    public static final String NO_RAFFLE_MESSAGE = "I'm sorry, but there isn't a raffle currently";
    protected static final Map<String, Raffle> raffles = new HashMap<>();

    @Override
    public boolean isApplicableToPM() {
        return false;
    }

    @Override
    public Category getCategory() {
        return Category.RAFFLE;
    }

    /**
     * gets the Roles that can manage Raffles in the supplied Guild
     * @param guild guild for which these roles apply
     * @return a List<Role> of all the Roles allowed to manage raffles
     */
    protected static List<Role> getAllowedManagementRoles(Guild guild) {
       return GuildManager.getGuildData(guild).getRaffleData().allowedRaffleRoles();
    }

    protected IRaffleData getRaffleData(Guild g){
        return GuildManager.getGuildData(g).getRaffleData();
    }

    protected ListenerAdapter raffleButtonListener = new ListenerAdapter() {
        @Override
        public void onButtonClick(@NotNull ButtonClickEvent event) {
            //not a raffle button, ignore
            if (!event.getComponentId().startsWith(Raffle.RAFFLE_BUTTON_PREFIX)) return;

            //no active raffle in channel clear the lingering buttons
            if(!raffles.containsKey(event.getChannel().getId())) {
                event.editMessage(event.getMessage()).setActionRows().queue();
                return;
            }
            boolean manager = isRaffleManager(event.getMember(),getAllowedManagementRoles(event.getGuild()));

            Raffle r = raffles.get(event.getChannel().getId());

            switch (event.getComponentId()) {
                case Raffle.ID_ENTER:
                    if (barred(event.getMember())) {
                        event.reply(String.format(AbstractRaffleCommand.BARRED_MESSAGE,
                                event.getMember().getEffectiveName()))
                                .setEphemeral(true).queue();
                    } else if (notActive(event.getMember())) {
                        event.reply(String.format(AbstractRaffleCommand.INACTIVE_MESSAGE,
                                event.getMember().getEffectiveName())).setEphemeral(true).queue();
                    } else {
                        switch (r.addEntry(event.getMember())){
                            case added:
                                event.reply("You have been entered into the raffle").setEphemeral(true).queue();
                                r.updateActiveMessage();
                                event.getChannel().sendMessage(event.getMember().getEffectiveName() + " has entered the raffle!").queue();
                                break;
                            case exists:
                                event.reply("Hey! you're already in! you can't have two tickets, sorry!")
                                        .setEphemeral(true).queue();
                                break;
                        }
                    }
                    break;
                case Raffle.ID_WITHDRAW:
                    if(r.removeEntry(event.getMember())){
                        event.reply("You have been Withdrawn from the Raffle. Better luck next time")
                                .setEphemeral(true).queue();
                        r.updateActiveMessage();
                        event.getChannel().sendMessage(event.getMember().getEffectiveName() + " has Withdrawn their name from the raffle...").queue();
                    } else {
                        event.reply("You were not in the raffle.").setEphemeral(true).queue();
                    }
                    break;
                case Raffle.ID_LOCK:
                    if(manager) {
                        event.reply("This will prevent further people from entering the raffle. Are you sure?\n" +
                                "Dismiss this message to cancel.")
                                .setEphemeral(true)
                                .addActionRow(Button.danger(Raffle.ID_CONFIRM_LOCK, "Yes! Close it!")
                                ).queue();
                    } else
                        //ignore button presses from non-managers
                        event.deferEdit().queue();
                    break;
                case Raffle.ID_CONFIRM_LOCK:
                    r.close();
                    event.editMessage("The Raffle is now Locked!").setActionRows().queue();
                    break;
                case Raffle.ID_END:
                    if(manager) {
                        event.reply("This will End the raffle and clear the Entry table. Only push this button" +
                                " once you're 100% done with the raffle and do not need to draw any more names!!\n" +
                                "THIS CANNOT BE UNDONE!\n" +
                                "Dismiss this message to cancel.")
                                .setEphemeral(true)
                                .addActionRow(Button.danger(Raffle.ID_CONFIRM_END, "Yes, We're done!"))
                                .queue();
                    } else
                        //ignore non-manager button presses
                        event.deferEdit().queue();
                    break;
                case Raffle.ID_CONFIRM_END:
                    endRaffle(event.getChannel().getId());
                    r.clearActiveMessage();
                    event.getChannel().sendMessage(
                            String.format("%s has ended the Raffle",event.getMember().getEffectiveName())).queue();
                    event.editMessage("The Raffle is now ended").setActionRows().queue();
                    break;
                case Raffle.ID_DRAW:
                    if(manager) {
                        if (r.getNumEntries() == 0) {
                            event.reply(r.isOpen() ? "Sorry, but there are no Entries yet..." : "There are no further entries")
                                    .setEphemeral(true).queue();
                            break;
                        }

                        if (r.isOpen()) {
                            event.reply("This will lock the raffle and not allow any further entries, are you sure?\n" +
                                    "Dismiss this message to cancel")
                                    .setEphemeral(true)
                                    .addActionRow(
                                            Button.danger(Raffle.ID_CONFIRM_DRAW, "Yes! lock the raffle and Draw!")
                                    ).queue();
                        } else {

                            Member winner = r.draw();
                            r.updateActiveMessage();
                            event.getChannel().sendMessage(String.format("Congratulations %s, you have won!!!!! " +
                                            "A member of the Moderation team will be reaching out to you shortly to handle the details!",
                                    winner.getAsMention())).queue();
                            if (r.getNumEntries() != 0) {
                                event.reply("There are still more entries, if you would like to draw an additional name" +
                                        " you may").setEphemeral(true).addActionRow(
                                                Button.danger(Raffle.ID_DRAW_AGAIN, "Draw Again!")
                                ).queue();
                            } else {
                                event.deferEdit().queue();
                            }
                        }
                    }else
                        //ignore non-manager button presses
                        event.deferEdit().queue();
                    break;
                case Raffle.ID_CONFIRM_DRAW:
                    r.close();
                    Member winner = r.draw();
                    r.updateActiveMessage();
                    event.getChannel().sendMessage(String.format("Congratulations %s, you have won!!!!! " +
                                    "A member of the Moderation team will be reaching out to you shortly to handle the details!",
                            winner.getAsMention())).queue();
                    if (r.getNumEntries() != 0) {
                        event.reply("There are still more entries, if you would like to draw an additional name" +
                                " you may").setEphemeral(true).addActionRows(Raffle.RAFFLE_BUTTONS_CLOSED).queue();
                    } else {
                        event.deferEdit().queue();
                    }
                    break;
                case Raffle.ID_DRAW_AGAIN:
                    winner = r.draw();
                    r.updateActiveMessage();
                    event.getChannel().sendMessage(String.format("Congratulations %s, you have won!!!!! " +
                                    "A member of the Moderation team will be reaching out to you shortly to handle the details!",
                            winner.getAsMention())).queue();
                    break;
                default:
                    event.reply("ranzer forgot to handle the " + event.getComponentId() +" button. yell at him").queue();
            }
        }
    };

    protected void endRaffle(String ID) {
        raffles.remove(ID);
        if(raffles.isEmpty()) GrimcoRaffleBot.getJDA().removeEventListener(raffleButtonListener);
    }

    private boolean isRaffleManager(Member m, List<Role> allowedRoles) {
        if (BotConfiguration.getInstance().getOwner().equals(m.getId())) return true; //bot owner bypass

        //check the roles for allowed roles or administrator perm
        for (Role role : m.getRoles()) {
            if (allowedRoles.contains(role)) return true;
            if (role.getPermissions().contains(Permission.ADMINISTRATOR)) return true;
        }

        return false;
    }

    protected boolean notActive(Member member) {
        return GuildManager.getGuildData(member.getGuild()).getMemberData(member).getXP()
                <
                getRaffleData(member.getGuild()).getRaffleXPThreshold();
    }

    protected boolean barred(Member member) {
        return GuildManager.getGuildData(member.getGuild()).getMemberData(member).isBannedFromRaffle();
    }
}
