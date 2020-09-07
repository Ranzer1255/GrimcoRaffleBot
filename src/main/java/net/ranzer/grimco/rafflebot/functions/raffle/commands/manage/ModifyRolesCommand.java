package net.ranzer.grimco.rafflebot.functions.raffle.commands.manage;

import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.commands.admin.HelpCommand;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.ranzer.grimco.rafflebot.util.StringUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class ModifyRolesCommand extends AbstractRaffleCommand implements Describable {

    @Override
    public void process(String[] args, MessageReceivedEvent event) {

        if (args.length==0){
            List<Role> roles = GuildManager.getGuildData(event.getGuild()).getRaffleData().allowedRaffleRoles();
            StringBuilder sb = new StringBuilder();

            for (Role r :
                    roles) {
                sb.append(r.getName()).append(" ,");
            }
            sb.delete(sb.length()-1,sb.length());

            event.getChannel().sendMessage(String.format(
                    "Allowed Roles: %s",
                    sb.toString()
            )).queue();
        } else {
            switch (args[0]){
                case "add":
                    List<Role> roles = event.getJDA().getRolesByName(
                            StringUtil.arrayToString(Arrays.copyOfRange(args, 1, args.length), " "),
                            true);
                    if (roles.isEmpty()) {
                        roleNotFound(event.getChannel());
                        return;
                    }
                    Role r = roles.get(0);

                    if (GuildManager.getGuildData(event.getGuild()).getRaffleData().addAllowedRole(r)){
                        roleAdd(event.getChannel(), r, true);
                    } else {
                        roleAdd(event.getChannel(), r, false);
                    }
                    break;
                case "remove":
                    roles = event.getJDA().getRolesByName(
                            StringUtil.arrayToString(Arrays.copyOfRange(args, 1, args.length), " "),
                            true);
                    if (roles.isEmpty()) {
                        roleNotFound(event.getChannel());
                        return;
                    }
                    r = roles.get(0);

                    if (GuildManager.getGuildData(event.getGuild()).getRaffleData().removeAllowedRole(r)){
                        roleRemove(event.getChannel(),r,true);
                    } else {
                        roleRemove(event.getChannel(),r,false);
                    }
                    break;
                default:
                    event.getChannel().sendMessage(HelpCommand.getDescription(this/*,event.getGuild()*/)).queue();
                    break;
            }
        }


    }

    private void roleRemove(MessageChannel channel, Role r, boolean b) {
        channel.sendMessage(String.format(
                "The role %s was %s",
                r.getName(),
                b?"removed":"not in the list"
        )).queue();
    }

    private void roleAdd(MessageChannel channel, Role r, boolean b) {
        channel.sendMessage(String.format(
                "The role %s was %s",
                r.getName(),
                b?"added":"already in the list"
        )).queue();
    }

    private void roleNotFound(MessageChannel channel) {
        channel.sendMessage(
                "I'm sorry but i cannot find that role"
        ).queue();
    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("role","roles");
    }

    @Override
    public String getShortDescription() {
        return "modify the list of roles that can run a raffle";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription()+"\n\n" +
                "add: add the specified role to the list\n" +
                "remove: remove the specified role from the list";
    }

    @Override
    public String getUsage(Guild g) {
        return String.format(
                "`%sraffle %s [{add | remove} <role>]",
                getPrefix(g),
                getName()
        );
    }

    @Override
    public Permission getPermissionRequirements() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public List<Role> getRoleRequirements(Guild guild) {
        return null;
    }
}
