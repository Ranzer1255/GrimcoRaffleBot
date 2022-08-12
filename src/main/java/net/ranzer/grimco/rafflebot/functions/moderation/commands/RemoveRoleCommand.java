package net.ranzer.grimco.rafflebot.functions.moderation.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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

public class RemoveRoleCommand extends BotCommand implements Describable {
	private final RoleManager rm;

	public RemoveRoleCommand(){
		rm = RoleManager.getInstance();
	}

	@Override
	protected boolean isApplicableToPM() {
		return false;
	}

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {

		Member m = event.getOption("user").getAsMember();
		Role role = event.getOption("role").getAsRole();
		try {
			rm.removeRole(role,m,event.getUser());
			event.reply(String.format(
					"Role %s successfully removed from %s",
					role.getName(),
					m.getEffectiveName()
			)).queue();
		} catch (InsufficientPermissionException pe) {
			event.reply(
					String.format("i'm sorry but i lack the `%s` permission in the server settings to do this",
							pe.getPermission().getName())).setEphemeral(true).queue();
		}catch (HierarchyException he){
			event.reply(
					"That role is above my pay-grade and I cannot Modify it! sorry..."
			).setEphemeral(true).queue();
		}

	}

//	@Override
//	protected void processPrefix(String[] args, MessageReceivedEvent event) {
//
//		if (args.length<2){
//			return;
//		}
//
//		//parse role
//		Role role = parseRole(event, args[0]);
//		if(role == null){
//			event.getChannel().sendMessage(String.format("i'm sorry i can't find role `%s`", args[0])).queue();
//			return;
//		}
//
//		//parse members to add role to
//		List<Member> users = event.getMessage().getMentionedMembers();
//		if(users.size()!=1){
//			event.getChannel().sendMessage("i'm sorry but you must mention the user/s to whom you would like to add this role").queue();
//			return;
//		}
//
//		//apply roles
//		for (Member m :	users) {
//			try {
//				rm.removeRole(role,m,event.getAuthor());
//				event.getChannel().sendMessage(String.format(
//						"Role %s successfully removed from %s",
//						role.getName(),
//						m.getEffectiveName()
//				)).queue();
//			} catch (InsufficientPermissionException pe) {
//				event.getChannel().sendMessage(
//						String.format("i'm sorry but i lack the `%s` permission in the server settings to do this",
//								pe.getPermission().getName())).queue();
//			}catch (HierarchyException he){
//				event.getChannel().sendMessage(
//						"That role is above my pay-grade and I cannot Modify it! sorry..."
//				).queue();
//			}
//		}
//	}

	@Override
	public Category getCategory() {
		return Category.ADMIN;
	}

	@Override
	public String getName() {
		return "remove_role";
	}

	@Override
	public String getShortDescription() {
		return "removes a timed role from a user";
	}

	@Override
	public String getLongDescription() {
		return getShortDescription();
	}

	@Override
	public String getUsage() {
		return String.format("`/%s <role> <mentioned user/s>`",getName());
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		SlashCommandData rtn = Commands.slash(getName(),getShortDescription());

		rtn.setDefaultEnabled(false);
		rtn.addOption(OptionType.ROLE,"role","Role to remove",true);
		rtn.addOption(OptionType.USER,"user", "user to remove role from", true);

		return rtn;
	}
}
