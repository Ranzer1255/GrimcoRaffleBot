package net.ranzer.grimco.rafflebot.functions.foldingathome.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.util.JsonURLReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FoldingAtHomeStatsCommand extends BotCommand implements Describable {

	@Override
	protected boolean isApplicableToPM() {
		return true;
	}

	@Override
	protected void process(String[] args, MessageReceivedEvent event) {

		final String TEAM_STATS_URL = "https://api.foldingathome.org/team/238767";
		final String TEAM_MEMBERS_URL = TEAM_STATS_URL+"/members";

		try {
			JSONObject teamStats = JsonURLReader.readJsonFromUrl(TEAM_STATS_URL);
			JSONArray membStats = JsonURLReader.readArrayFromUrl(TEAM_MEMBERS_URL);

			EmbedBuilder eb = new EmbedBuilder();

			String FAH_URL = "https://foldingathome.org/start-folding/";
			eb.setAuthor(teamStats.getString("name")+" F@H Stats",FAH_URL)
					.setTitle(String.format("Team number: %d",teamStats.getInt("id")), FAH_URL)
					.setDescription(
							String.format("Score: %,d\n"+
										  "Work Units: %,d\n" +
										  "Rank: %,d",
									      teamStats.getInt("score"),
									      teamStats.getInt("wus"),
									      teamStats.getInt("rank"))
					)
					.setColor(getCategory().COLOR);
			for (int i = 1; i < membStats.length(); i++) {
				JSONArray row = membStats.getJSONArray(i);

				eb.addField(row.getString(0),
							String.format("Score: %,d\nWU: %,d",
									row.getInt(3),
									row.getInt(4)),
						true);
			}

			eb.setFooter("Join us by clicking the team number",null);

			event.getChannel().sendMessageEmbeds(eb.build()).queue();

		} catch (IOException e) {
			event.getChannel().sendMessage("There was an issue reaching the Folding@Home API. please try again later.").queue();
		} catch (JSONException e){
			event.getChannel().sendMessage("Folding@Home has changed their API structure (again) and Ranzer is unaware. " +
					"please give him this message so that he can correct the issue as soon as possible\n\n" +
					"```\n" +
					e.getLocalizedMessage() +
					"\n```").queue();
		}

	}

	@Override
	public Category getCategory() {
		return Category.MISC;
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("folding", "fold");
	}

	@Override
	public String getShortDescription() {
		return "TLoG's current Folding@Home stats";
	}

	@Override
	public String getLongDescription() {
		return getShortDescription()+"\n\n" +
				"Get our current stats from Folding At Home's API and Member rankings";
	}
}
