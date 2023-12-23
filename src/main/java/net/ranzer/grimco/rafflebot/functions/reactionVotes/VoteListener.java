package net.ranzer.grimco.rafflebot.functions.reactionVotes;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoteListener {

	private static VoteListener vl;
	private Map<Message,Vote> activeVotes;
	private static final String VOTE_BUTTON_PREFIX = "vl_";

	public static final String ID_OPTION_PREFIX = VOTE_BUTTON_PREFIX + "option_";
	public static final String ID_END_EARLY = VOTE_BUTTON_PREFIX + "end";

	public static VoteListener getListener(){
		if (vl==null){
			vl = new VoteListener();
		}
		return vl;
	}

	private VoteListener(){}

	public static List<ActionRow> getButtons(int numOfOptions){
		List<ActionRow> rtn = new ArrayList<>();

		List<Button> options = new ArrayList<>();
		for (int i = 1; i <= numOfOptions; i++) {
			options.add(Button.primary(ID_OPTION_PREFIX+i,"Option: "+i));
		}

		rtn.add(ActionRow.of(options));
		rtn.add(ActionRow.of(Button.danger(ID_END_EARLY,"End Early?")));

		return rtn;
	}

	private static class ButtonHandler extends ListenerAdapter {
		@Override
		public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
			//not a vote button do not handle here!
			if (!event.getComponentId().startsWith(VOTE_BUTTON_PREFIX)) return;

			Vote vote = VoteListener.getListener().activeVotes.get(event.getMessage());


		}
	}

}
