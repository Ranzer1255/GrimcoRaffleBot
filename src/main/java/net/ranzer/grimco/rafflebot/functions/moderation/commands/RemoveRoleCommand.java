package net.ranzer.grimco.rafflebot.functions.moderation.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.functions.moderation.RoleManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RemoveRoleCommand extends BotCommand implements Describable {
	private RoleManager rm;

	public RemoveRoleCommand(){
		rm = RoleManager.getInstance();
	}

	@Override
	protected boolean isApplicableToPM() {
		return false;
	}

	@Override
	protected void process(String[] args, MessageReceivedEvent event) {

		System.out.println(Arrays.toString(args));

		if (args.length<3){
			return;
		}

		//parse role
		List<Role> list = event.getJDA().getRolesByName(args[0],true);
		if(list.size()==0){
			event.getChannel().sendMessage(String.format("i'm sorry i can't find role `%s`", args[0])).queue();
			return;
		}
		Role role = list.get(0);

		//parse members to add role to
		List<Member> users = event.getMessage().getMentionedMembers();
		if(users.size()!=1){
			event.getChannel().sendMessage("i'm sorry but you must mention the user/s to whom you would like to add this role").queue();
			return;
		}

		//apply roles
		for (Member m :	users) {
			try {
				rm.removeRole(role,m);
			} catch (InsufficientPermissionException pe) {
				event.getChannel().sendMessage(
						String.format("i'm sorry but i lack the `%s` permission in the server settings to do this",
								pe.getPermission().getName())).queue();
			}
		}
	}

	@Override
	public Category getCategory() {
		return Category.ADMIN;
	}

	@Override
	public List<String> getAlias() {
		return Collections.singletonList("removerole");
	}

	@Override
	public String getShortDescription() {
		return "removes a role to a user for a number of days";
	}

	@Override
	public String getLongDescription() {
		return getShortDescription();
	}

	@Override
	public Permission getPermissionRequirements() {
		return Permission.ADMINISTRATOR;
	}

	@Override
	protected List<Role> getRoleRequirements(Guild guild) {
		return GuildManager.getGuildData(guild).getModRoles();
	}

	@Override
	public String getUsage(Guild g) {
		return String.format("`%s%s <role> <mentioned user/s>`",getPrefix(g),getName());
	}
}
