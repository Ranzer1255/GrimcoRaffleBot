package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayer;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

public class VolCommand extends AbstractMusicSubCommand implements Describable{

	public static final String NOT_IN_VOICE = "You must be listening to adjust the volume.";
	public static final String NOT_IN_RANGE = "I'm sorry please use an Integer between 1-150";
	private final String LEVEL = "level";

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		int level = event.getOption(LEVEL, -1,OptionMapping::getAsInt);
		GuildPlayer gp = GuildPlayerManager.getPlayer(event.getGuild());
		if (level==-1){
			event.reply(getVolMessage(gp.getVol())).setEphemeral(true).queue();
			return;
		}
		if(notInSameAudioChannel(event.getMember())){
			event.reply(NOT_IN_VOICE).setEphemeral(true).queue();
			return;
		}
		try{
			gp.setVol(level);
			event.reply(getVolMessage(gp.getVol())).queue();
		}catch (NumberFormatException e){
			event.reply(NOT_IN_RANGE).setEphemeral(true).queue();
		}
	}

	@Override
	public String getName() {
		return "vol";
	}

	@Override
	public String getShortDescription() {
		return "Adjust the volume of the song on a scale from 1-150";
	}

	@Override
	public String getLongDescription() {
		return super.getLongDescription()+
				"not supplying a value will give the current volume setting\n\n"
				+ "volume goes to values between `101` and `150` are \"boosted\" beyond the original track's volume use with caution\n\n"
				+ "you must be in the same voice channel to adjust the volume";
	}

	@Override
	public String getUsage() {
		return String.format("`/music %s [<1-150>]`", getName());
	}

	@Override
	protected SubcommandData getSubCommandData() {
		SubcommandData rtn = super.getSubCommandData();
		OptionData od = new OptionData(OptionType.INTEGER,LEVEL,getShortDescription(),false).setRequiredRange(1,150);
		rtn.addOptions(od);

		return rtn;
	}

	private Message getVolMessage(int vol) {
		MessageBuilder mb = new MessageBuilder();

		mb.append(String.format("Volume set to %d\n", vol))
				.append("```\n")
				.append("*-------------------------*--boost---*\n")
				.append(volumeBar(vol)).append("\n")
				.append("*-------------------------*----------*\n")
				.append("```");
		return mb.build();
	}

	private CharSequence volumeBar(int vol) {
		StringBuilder rtn = new StringBuilder();
		rtn.append("*|");

		//not boosted
		if (vol<=100) {

			//number of bars to add (if the math comes out to neg set to 0
			int volBars = Math.max(((vol / 4) - 2), 0);

			//add bars
			rtn.append("=".repeat(volBars));
			rtn.append('|');

			//add blank space
			rtn.append(" ".repeat(23 - volBars));

			//fill out blank boost space
			rtn.append("*          *");

			//boosted volume
		} else {
			int boost = vol-100;
			int boostBars = boost/5;

			//fill in full standard bar
			rtn.append("========================*");

			//add boost bars
			rtn.append("=".repeat(Math.max(0, boostBars - 1)));
			rtn.append("|");

			//add blank space
			rtn.append(" ".repeat(Math.max(0, 10 - boostBars)));
			rtn.append('*');
		}
		return rtn.toString();
	}
}
