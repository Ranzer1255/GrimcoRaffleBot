package net.ranzer.grimco.rafflebot.functions.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.ranzer.grimco.rafflebot.GrimcoRaffleBot;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.admin.HelpCommand;
import net.ranzer.grimco.rafflebot.commands.admin.InfoCommand;
import net.ranzer.grimco.rafflebot.commands.admin.PingCommand;
import net.ranzer.grimco.rafflebot.functions.dice.commands.DiceCommand;
import net.ranzer.grimco.rafflebot.functions.dice.commands.FateDiceCommand;
import net.ranzer.grimco.rafflebot.functions.draconic.commands.DraconicTranslateCommand;
import net.ranzer.grimco.rafflebot.functions.excuses.commands.ExcusesCommand;
import net.ranzer.grimco.rafflebot.functions.foldingathome.commands.FoldingAtHomeStatsCommand;
import net.ranzer.grimco.rafflebot.functions.moderation.commands.AddRoleCommand;
import net.ranzer.grimco.rafflebot.functions.moderation.commands.RemoveRoleCommand;
import net.ranzer.grimco.rafflebot.functions.music.commands.MusicCommand;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.manage.RaffleManagementCommand;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.run.RaffleEnterCommand;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.run.RaffleWithdrawCommand;
import net.ranzer.grimco.rafflebot.util.Logging;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandListener extends ListenerAdapter {
	private static CommandListener cl;
	private final List<BotCommand> cmds = new ArrayList<>();

	public static CommandListener getInstance() {
		if (cl == null) cl = new CommandListener();
		return cl;
	}

	private CommandListener() {
		this.addCommand(new HelpCommand())
			.addCommand(new InfoCommand())
			.addCommand(new PingCommand())
			.addCommand(new AddRoleCommand())
			.addCommand(new RemoveRoleCommand())
			.addCommand(new DiceCommand())
			.addCommand(new FateDiceCommand())
			.addCommand(new DraconicTranslateCommand())
			.addCommand(new RaffleManagementCommand())
			.addCommand(new RaffleEnterCommand())
			.addCommand(new RaffleWithdrawCommand())
			.addCommand(new FoldingAtHomeStatsCommand())
			.addCommand(new MusicCommand())
		    .addCommand(new ExcusesCommand());

		List<CommandData> slashCmds = new ArrayList<>();
		for (BotCommand cmd : cmds) {
			if (cmd.getSlashCommandData() != null) {
				slashCmds.add(cmd.getSlashCommandData());
			}
		}

		for (Guild g : GrimcoRaffleBot.getJDA().getGuilds()) {
			g.updateCommands().addCommands(slashCmds).queue();
		}
	}

	private CommandListener addCommand(BotCommand cmd) {
		this.cmds.add(cmd);
		return this;
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		Logging.debug("looking for a slash command");
		Optional<BotCommand> c = cmds.stream().filter(cmd -> cmd.getName().equals(event.getName())).findFirst();
		if (c.isPresent()) {
			Logging.debug("found one");
			for (OptionMapping o : event.getOptions()) {
				Logging.debug(String.format("%s: %s", o.getName(), o.getAsString()));
			}
		}
		c.ifPresent(botCommand -> callSlashCommand(event, botCommand));
	}

	private void callSlashCommand(SlashCommandInteractionEvent event, BotCommand cmd) {
		new Thread() {
			@Override
			public void run() {
				cmd.runSlashCommand(event);
				interrupt();
			}
		}.start();
	}

	public List<BotCommand> getCommands() {

		return cmds;
	}

}
