package grimco.ranzer.rafflebot.commands.admin;

import java.util.Collections;
import java.util.List;

import grimco.ranzer.rafflebot.commands.BotCommand;
import grimco.ranzer.rafflebot.config.BotConfiguration;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ShutdownCommand extends BotCommand { //TODO this command to be called by GrimCo admins only
	
	@Override
	public void process(String[] args,  MessageReceivedEvent event) {
		if (event.getAuthor()!=event.getJDA().getUserById(BotConfiguration.getInstance().getOwner())){
			noPermission(event);
			return;
		}
		event.getChannel().sendMessage("if you insist boss.... *blerg*").complete();
		try {Thread.sleep(1000L);} catch (InterruptedException e) {}
		event.getJDA().shutdown();
		System.exit(0);
	}

	@Override
	public List<String> getAlias() {
		return Collections.singletonList("vdri");//this is "sleep" in draconic
	}

	@Override
	public boolean isApplicableToPM() {
		return true;
	}
}