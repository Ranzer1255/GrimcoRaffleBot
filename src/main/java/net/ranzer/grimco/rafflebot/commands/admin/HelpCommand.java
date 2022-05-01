package net.ranzer.grimco.rafflebot.commands.admin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.listeners.CommandListener;
import net.ranzer.grimco.rafflebot.util.Logging;
import net.ranzer.grimco.rafflebot.util.StringUtil;

import java.util.*;

public class HelpCommand extends BotCommand implements Describable{

	
	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {
		Logging.debug("Help called");
		CommandListener cmds = CommandListener.getInstance();

		MessageBuilder mb = new MessageBuilder();
		
		//single command help line
		if(args.length==1){
			Optional<Describable> opt = getDescribables(cmds.getCommands()).stream().filter(cc -> cc.getAlias()
					.contains(args[0].toLowerCase())).findFirst();
			if(opt.isPresent()){
				Describable d = opt.get();
				event.getAuthor().openPrivateChannel().complete().sendMessageEmbeds(getDescription(d/*,event.getGuild()*/)).queue();
			}
		}else if(args.length == 2){
			Optional<Describable> opt = getDescribables(cmds.getCommands()).stream().filter(cc -> cc.getAlias()
					.contains(args[0].toLowerCase())).findFirst();
			if(opt.isPresent()&&opt.get().hasSubcommands()){
				Describable baseCommand = opt.get();
				Optional<Describable> subOpt = getDescribables(baseCommand.getSubcommands()).stream()
						.filter(cc -> cc.getAlias().contains(args[1].toLowerCase())).findFirst();
				subOpt.ifPresent(describable -> event.getAuthor().openPrivateChannel().complete()
						.sendMessageEmbeds(
								getDescription(describable/*, event.getGuild()*/)).queue());
			}
		//full command list	
		}else{
			
			Map<Category, List<Describable>> categorised = new HashMap<>();
			
			for (Describable d : getDescribables(cmds.getCommands())) {
				categorised.computeIfAbsent(d.getCategory(), k -> new ArrayList<>());
				categorised.get(d.getCategory()).add(d);
			}	
			StringBuilder sb = new StringBuilder();
			EmbedBuilder eb = new EmbedBuilder();
			
			eb.setAuthor("Full Command List", null, null);
			if (event.isFromGuild()) {
				event.getGuild().retrieveMember(event.getJDA().getSelfUser()).queue(m->eb.setColor(m.getColor()));
			}
			categorised.keySet().stream().sorted((o1,o2)->
					o1.NAME.compareToIgnoreCase(o2.name())				
			).forEachOrdered(cat -> {
				sb.append(String.format("**__%s__**\n", cat.NAME));
				categorised.get(cat).stream().sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName())
				).forEachOrdered(d -> sb.append(String.format("**%s:** %s\n", d.getName(), d.getShortDescription())));
			sb.append("\n");
			});
			eb.setDescription(sb.toString());
			mb.setEmbeds(eb.build());

			event.getAuthor().openPrivateChannel().complete().sendMessage(mb.build()).queue();
		}
	}

	public static MessageEmbed getDescription(Describable d/*, Guild g*/) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(d.getName(), null, null);
		eb.setDescription((d.getLongDescription()!=null)?d.getLongDescription():"long description WIP");
		eb.setColor(d.getCategory().COLOR);
//		eb.addField("Usage",d.getUsage(g)!=null?d.getUsage(g):"usage wip",false);
		eb.addField("Other Aliases",
				(d.getAlias().size()-1)!=0 ? 
						"`"+StringUtil.arrayToString(d.getAlias().subList(1, d.getAlias().size()), "`, `")+"`":
						"*none*",
				true );
		eb.addField("Category", d.getCategory().toString(), true);
		if (d.getPermissionRequirements() != null) {
			eb.addField("Role Requirement", d.getPermissionRequirements().getName(), true);
		}
		
		return eb.build();
	}

	@Override
	public String getUsage(Guild g) {

		return "`"+getPrefix(g)+"help [<command>]`";
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("help", "h");
	}

	@Override
	public String getShortDescription() {
		return "Gives a list of available command";
	}

	@Override
	public Category getCategory() {
		return Category.ADMIN;
	}
	
	@Override
	public String getLongDescription() {
		
		return getShortDescription()+"\n\n"
				+ "when given a command as an argument, `help` will generate a help page for that command and return it\n\n"
				+ "__**Usage syntax**__\n"
				+ "arguments in `<>` are to be substituted with the requested value\n"
				+ "arguments in `[]` are optional and can be ignored\n"
				+ "arguments in `{}` mean pick one from the list separated by `|`";
	}

	private List<Describable> getDescribables(List<BotCommand> list){
		List<Describable> rtn = new ArrayList<>();
			
		for (BotCommand cmd :list) {
			if(cmd instanceof Describable){
				rtn.add((Describable) cmd);
			}
		}
		
		return rtn;
	}

	@Override
	public boolean isApplicableToPM() {
		return true;
	}
}
