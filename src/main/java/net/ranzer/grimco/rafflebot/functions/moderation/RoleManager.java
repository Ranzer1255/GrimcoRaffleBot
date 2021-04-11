package net.ranzer.grimco.rafflebot.functions.moderation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.ranzer.grimco.rafflebot.GrimcoRaffleBot;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.util.Logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RoleManager {
	private static RoleManager instance;
	private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

	private RoleManager(){//TODO fix this to use the non-cached version of get.member
		for (Guild guild : GrimcoRaffleBot.getJDA().getGuilds()) {
			//TODO loop thorough ever entry in the database, not every member in the guild....
			for (Member member : guild.getMembers()) {
				if (GuildManager.getGuildData(guild).getMemberData(member).getTimedRoles().entrySet().isEmpty()){
					continue;
				}
				for (Map.Entry<Role,Long> entry : GuildManager.getGuildData(guild).getMemberData(member).getTimedRoles().entrySet()) {
					Date dateToRemove = new Date(entry.getValue());

					if (dateToRemove.before(new Date())) {
						Logging.debug(String.format("we're past %s removeing role %s from %s",
								new SimpleDateFormat("MM/dd HH:mm:ss").format(dateToRemove),
								entry.getKey().getName(),
								member.getEffectiveName()));

						removeRole(entry.getKey(),member);
						continue;
					}

					Logging.debug(String.format("adding persisted scheduled task for member %s, scheduled to be removed at %s",
							member.getEffectiveName(),
							new SimpleDateFormat("MM/dd HH:mm:ss").format(dateToRemove)));

					scheduleRemoval(member, entry.getKey(), entry.getValue()-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
				}
			}
		}
	}

	private void scheduleRemoval(Member member, Role role, long time, TimeUnit timeUnit) {
		ses.schedule(() -> removeRole(role,member),time, timeUnit);
	}

	public static RoleManager getInstance() {
		if (instance==null){
			instance=new RoleManager();
		}
		return instance;
	}

	public void addRole(Role role, Member member, int daysToRemove, User requester){
		Logging.debug(String.format("adding role %s to %s and removing in %s days", role.getName(),member.getEffectiveName(),daysToRemove));

		//add role to member in discord
		member.getGuild().addRoleToMember(member,role).reason(String.format(
				"Added by order of %s, will be automatically removed in %d %s",
				requester.getName(),
				daysToRemove,
				daysToRemove==1?"day":"days"
		)).queue();

		//schedule task to remove role
		scheduleRemoval(member,role,daysToRemove,TimeUnit.DAYS);

		//save info to DB
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE,daysToRemove);
		GuildManager.getGuildData(member.getGuild()).getMemberData(member).addTimedRole(role, cal.getTimeInMillis());
	}

	/**
	 * removes the role based on the timer set in addRole()
	 * @param role to be added
	 * @param member to which the role should be applied
	 */
	private void removeRole(Role role, Member member){
		Logging.debug(String.format("removing role %s from %s",role.getName(),member.getEffectiveName()));
		member.getGuild().removeRoleFromMember(member,role).reason("removed by Timer").queue();
		GuildManager.getGuildData(member.getGuild()).getMemberData(member).removedTimedRole(role);
	}

	/**
	 * admin override to remove a role before the timer is up. the action will be documented in the Audit logs
	 * @param role to be removed
	 * @param member from which the role should be removed
	 * @param requester the user who ordered the removal of the role
	 */
	public void removeRole(Role role, Member member,User requester){
		Logging.debug(String.format("removing role %s from %s",role.getName(),member.getEffectiveName()));
		member.getGuild().removeRoleFromMember(member,role).reason(String.format(
				"Removed by order of %s",
				requester.getName()
		)).queue();
		GuildManager.getGuildData(member.getGuild()).getMemberData(member).removedTimedRole(role);
	}
}
