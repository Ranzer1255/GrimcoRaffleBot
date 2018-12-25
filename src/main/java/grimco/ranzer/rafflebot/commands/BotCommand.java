package grimco.ranzer.rafflebot.commands;

import java.util.List;

import grimco.ranzer.rafflebot.config.BotConfiguration;
import grimco.ranzer.rafflebot.data.GuildManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class BotCommand {

	
	private static final String NO_PERMISSION_MESSAGE = "You're not my player! You can't tell me what to do!";

	public static String getPrefix(Guild guild) {
		if (guild==null){
			return "";
		}
		return GuildManager.getPrefix(guild);
	}

	public void runCommand(String[] args, MessageReceivedEvent event){
		if (!event.getAuthor().getId().equals(BotConfiguration.getInstance().getOwner())) { //override all permission checks if its me
			if (!hasPermission(args, event)) {
				noPermission(event);
				return;
			} 
		}
		if(event.getGuild()==null && !isApplicableToPM()){
			event.getChannel().sendMessage("This command cannot be used in Private channels").queue();
			return;
		}
		process(args, event);
	}
	
	abstract public boolean isApplicableToPM();
	
	abstract public void process(String[] args, MessageReceivedEvent event);
	
	abstract public List<String> getAlias();

	/*
	TODO rewrite this so that it checks if user has
	 */
	private boolean hasPermission(String[] args, MessageReceivedEvent event) {
		if(getPermissionRequirements()==null)
			return hasRoleRequirements(args, event);
		for (Role role : event.getGuild().getMember(event.getAuthor()).getRoles()) {
			if(role.getPermissions().contains(getPermissionRequirements())){
				return true;
			}
		}
		return false;
	}

	private boolean hasRoleRequirements(String[] args, MessageReceivedEvent event) {
		if(getRoleRequirements()==null)
			return true;
		for(Role role : event.getGuild().getMember(event.getAuthor()).getRoles()){
			if(getRoleRequirements().contains(role))
				return true;
		}
		
		
		return false;
	}
	
	public List<Role> getRoleRequirements() {
		return null;
	}

	public Permission getPermissionRequirements() {
		return null;
	}

	protected void noPermission(MessageReceivedEvent event) {
		event.getChannel().sendMessage(event.getAuthor().getAsMention()+" "+NO_PERMISSION_MESSAGE).queue();
		
	}
	
	public String invalidUsage(Guild g){
		return null;
	}

	public BotCommand getCommand() {
		return this;
	}
	
	public String getName(){
		return getAlias().get(0);
	}
	
	public boolean hasSubcommands(){
		return false;
	}
	
	public List<BotCommand> getSubcommands(){
		return null;
	}
	public String getUsage(Guild g) {
		return String.format("`%s%s`", getPrefix(g),getName());
	}

}
