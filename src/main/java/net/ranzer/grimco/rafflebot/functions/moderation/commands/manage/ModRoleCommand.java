package net.ranzer.grimco.rafflebot.functions.moderation.commands.manage;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.commands.admin.HelpCommand;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.util.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModRoleCommand extends BotCommand implements Describable {
	@Override
	protected boolean isApplicableToPM() {
		return false;
	}

	@Override
	protected void process(String[] args, MessageReceivedEvent event) {

		if (args.length == 0) {
			List<Role> roles = GuildManager.getGuildData(event.getGuild()).getModRoles();
			StringBuilder sb = new StringBuilder();

			String message;
			if(roles.isEmpty()){
				message = "no roles on the list, only members with *ADMINISTRATOR* permission may use moderation commands";
			} else {
				for (Role r : roles) {
					sb.append(r.getName()).append(" ,");
				}
				sb.delete(sb.length() - 1, sb.length());
				message = String.format("Allowed Roles: %s", sb);
			}
			event.getChannel().sendMessage(message).queue();
		} else {
			switch (args[0]) {
				case "add":
					List<Role> roles = event.getJDA().getRolesByName(
						StringUtil.arrayToString(Arrays.copyOfRange(args, 1, args.length), " "),
						true);
					if (roles.isEmpty()) {
						roleNotFound(event.getChannel());
						return;
					}
					Role r = roles.get(0);

					if (GuildManager.getGuildData(event.getGuild()).addModRole(r)) {
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

					if (GuildManager.getGuildData(event.getGuild()).removeModRole(r)) {
						roleRemove(event.getChannel(), r, true);
					} else {
						roleRemove(event.getChannel(), r, false);
					}
					break;
				default:
					event.getChannel().sendMessage(HelpCommand.getDescription(this/*, event.getGuild()*/)).queue();
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
	public Category getCategory() {
		return Category.ADMIN;
	}

	@Override
	public List<String> getAlias() {
		return Collections.singletonList("modrole");
	}

	@Override
	public String getShortDescription() {
		return "adds and removes roles that are allowed to moderate this server";
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
				"`%s%s [{add | remove} <role>]",
				getPrefix(g),
				getName()
		);
	}
}
