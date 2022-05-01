package net.ranzer.grimco.rafflebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.data.GuildManager;

import java.util.List;

public abstract class BotCommand {

	//todo rebrand this to something *less* caex and more Grimco
	private static final String NO_PERMISSION_MESSAGE = "You're not my player! You can't tell me what to do!";

	public static String getPrefix(Guild guild) {
		if (guild==null){
			return "";
		}
		return GuildManager.getPrefix(guild);
	}

	public void runSlashCommand(SlashCommandInteractionEvent event){ //TODO check perms
		processSlash(event);
	}

	public void runPrefixCommand(String[] args, MessageReceivedEvent event){
		if (!event.getAuthor().getId().equals(BotConfiguration.getInstance().getOwner())) { //override all permission checks if its me
			if (!hasPermissionToRun(event)) {
				noPermission(event);
				return;
			} 
		}
		if(!event.isFromGuild() && !isApplicableToPM()){
			event.getChannel().sendMessage("This command cannot be used in Private channels").queue();
			return;
		}
		processPrefix(args, event);
	}
	
	protected abstract boolean isApplicableToPM();
	
	protected abstract void processPrefix(String[] args, MessageReceivedEvent event);

	protected void processSlash(SlashCommandInteractionEvent event){}

	abstract public List<String> getAlias();

	private boolean hasPermissionToRun(MessageReceivedEvent event) {

		if(getPermissionRequirements()==null && getRoleRequirements(event.getGuild())==null){
			return true;
		} else if(getPermissionRequirements()!=null && getRoleRequirements(event.getGuild())==null){
			return checkPermissionRequirements(event);
		} else if(getPermissionRequirements()==null && getRoleRequirements(event.getGuild())!= null){
			return checkRoleRequirements(event);
		} else {
			return (checkPermissionRequirements(event) || checkRoleRequirements(event));
		}
	}

	private boolean checkPermissionRequirements(MessageReceivedEvent event) {
		for (Role role : event.getMember().getRoles()) {
			if(getPermissionRequirements()!=null) {
				if (role.getPermissions().contains(getPermissionRequirements())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkRoleRequirements(MessageReceivedEvent event) {
		for(Role role : event.getMember().getRoles()){
			if(getRoleRequirements(event.getGuild()).contains(role))
				return true;
		}


		return false;
	}

	protected List<Role> getRoleRequirements(Guild guild) {
		return null;
	}

	public Permission getPermissionRequirements() {
		return null;
	}

	protected void noPermission(MessageReceivedEvent event) {
		//TODO rename this to noPermissionMessage and refactor to require a User object instead of event and return a string. handle the *actual* responce one level up.
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

	protected Role parseRole(MessageReceivedEvent event, String role) {

		//parse role
		List<Role> list = event.getJDA().getRolesByName(role,true);
		if(list.size()==0){
			return null;
		}

		return list.get(0);
	}

	public SlashCommandData getSlashCommandData() {
		return null;
	}
}
