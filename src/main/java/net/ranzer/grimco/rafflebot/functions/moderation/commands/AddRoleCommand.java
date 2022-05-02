package net.ranzer.grimco.rafflebot.functions.moderation.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.functions.moderation.RoleManager;

import java.util.Collections;
import java.util.List;

public class AddRoleCommand extends BotCommand implements Describable {
	private final RoleManager rm;
	public AddRoleCommand(){
		rm = RoleManager.getInstance();
	}

	@Override
	protected boolean isApplicableToPM() {
		return false;
	}

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		Role role = event.getOption("role").getAsRole();
		Member m = event.getOption("user").getAsMember();
		int days = event.getOption("days").getAsInt();
		User requester = event.getUser();

		try {
			rm.addRole(role,m,days,requester);
			event.reply(String.format(
					"Role %s successfully added to %s for %d %s",
					role.getName(),
					m.getEffectiveName(),
					days,
					days==1?"day":"days"
			)).queue();
		} catch (InsufficientPermissionException pe) {
			event.reply(
					String.format("i'm sorry but i lack the `%s` permission in the server settings to do this",
							pe.getPermission().getName())).setEphemeral(true).queue();
		} catch (HierarchyException he){
			event.reply(
					"That role or user is above my pay-grade and I cannot Modify it! sorry..."
			).setEphemeral(true).queue();
		}
	}

	@Override
	protected void processPrefix(String[] args, MessageReceivedEvent event) {

		if (args.length<3){
			return;
		}

		//parse role
		Role role = parseRole(event, args[0]);
		if(role == null){
			event.getChannel().sendMessage(String.format("i'm sorry i can't find role `%s`", args[0])).queue();
			return;
		}

		//parse members to add role to
		List<Member> users = event.getMessage().getMentionedMembers();
		if(users.size()!=1){
			event.getChannel().sendMessage("i'm sorry but you must mention the user/s to whom you would like to add this role").queue();
			return;
		}

		//parse number of days
		int days;
		try {
			days = Integer.parseInt(args[args.length-1]);
		} catch (NumberFormatException e) {
			event.getChannel().sendMessage(String.format("i'm sorry but i didn't understand `%s`",args[args.length-1])).queue();
			return;
		}

		User requester = event.getAuthor();

		//apply roles
		for (Member m :	users) {
			try {
				rm.addRole(role,m,days,requester);
				event.getChannel().sendMessage(String.format(
						"Role %s successfully added to %s for %d %s",
						role.getName(),
						m.getEffectiveName(),
						days,
						days==1?"day":"days"
				)).queue();
			} catch (InsufficientPermissionException pe) {
				event.getChannel().sendMessage(
						String.format("i'm sorry but i lack the `%s` permission in the server settings to do this",
						pe.getPermission().getName())).queue();
			} catch (HierarchyException he){
				event.getChannel().sendMessage(
					"That role or user is above my pay-grade and I cannot Modify it! sorry..."
				).queue();
			}
		}
	}

	@Override
	public Category getCategory() {
		return Category.ADMIN;
	}

	@Override
	public List<String> getAlias() {
		return Collections.singletonList("timed_role");
	}

	@Override
	public String getShortDescription() {
		return "adds a role to a user for a number of days";
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
	public List<Role> getRoleRequirements(Guild guild) {
		return GuildManager.getGuildData(guild).getModRoles();
	}

	@Override
	public String getUsage(Guild g) {
		return String.format("`%s%s <role> <mentioned user/s> <num of days>`",getPrefix(g),getName());
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		SlashCommandData rtn = Commands.slash(getName(),getShortDescription());

		rtn.addOption(OptionType.ROLE,"role","Role to add to user",true);
		rtn.addOption(OptionType.USER, "user", "User to ad role to", true);
		rtn.addOption(OptionType.INTEGER, "days", "Number of Days to apply role",true);

		rtn.setDefaultEnabled(false);

		return rtn;
	}
}
