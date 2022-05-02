package net.ranzer.grimco.rafflebot.functions.raffle;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Raffle {

    public static final String RAFFLE_BUTTON_PREFIX = "rfl_";

    public static final String ID_ENTER =         RAFFLE_BUTTON_PREFIX + "enter";
    public static final String ID_WITHDRAW =      RAFFLE_BUTTON_PREFIX + "withdraw";
    public static final String ID_DRAW =          RAFFLE_BUTTON_PREFIX + "draw";
    public static final String ID_LOCK =          RAFFLE_BUTTON_PREFIX + "close";
    public static final String ID_END =           RAFFLE_BUTTON_PREFIX + "end";
    public static final String ID_CONFIRM_END =   RAFFLE_BUTTON_PREFIX + "confirm_end";
    public static final String ID_CONFIRM_LOCK =  RAFFLE_BUTTON_PREFIX + "confirm_lock";
    public static final String ID_CONFIRM_DRAW =  RAFFLE_BUTTON_PREFIX + "confirm_draw";
    public static final String ID_DRAW_AGAIN =    RAFFLE_BUTTON_PREFIX + "draw_again";

    public static final List<ActionRow> RAFFLE_BUTTONS_OPEN = Arrays.asList(
            ActionRow.of(
                    Button.primary(ID_ENTER, "Enter"),
                    Button.secondary(ID_WITHDRAW, "Withdraw")
            ),
            ActionRow.of(
                    Button.danger(ID_DRAW, "Draw!")
            ),
            ActionRow.of(
                    Button.danger(ID_LOCK, "Lock Entries"),
                    Button.danger(ID_END, "End")
            )
    );

    public static final List<ActionRow> RAFFLE_BUTTONS_CLOSED = Collections.singletonList(
            ActionRow.of(
                    Button.danger(ID_DRAW, "Draw!"),
                    Button.danger(ID_END, "End")
            )
    );



	private boolean isOpen;
    private final String prize;
    private final List<Member> entries = new ArrayList<>();
    private Message activeMessage;

    public Raffle(String prize){
        isOpen = true;
        this.prize=prize;
    }

    //? entry gating is handled at the Command level
    /**
     * Add entrant to the Raffle
     *
     * @param entrant Member to be added to the raffle
     * @return true if the Raffle didn't already have this entrant
     */
    public EntryResult addEntry(Member entrant){
        if(!isOpen) return EntryResult.closed;
        if (!entries.contains(entrant)) {
            entries.add(entrant);
            return EntryResult.added;
        }
        else {
            return EntryResult.exists;
        }
    }

    /**
     * removes an entrant from the list if they were there to begin with
     * @param entrant entrant to be removed
     * @return true if entrant was present in the list
     */
    public boolean removeEntry(Member entrant){
        return entries.remove(entrant);
    }

    /**
     * pulls a random entrant from the Raffle and removes their entry from the list;
     * @return Random entrant removed from List
     */
    public Member draw(){
        return entries.remove(ThreadLocalRandom.current().nextInt(entries.size()));
    }

    public int getNumEntries(){
        return entries.size();
    }

    public boolean isOpen(){
        return isOpen;
    }

    public void close(){
        isOpen=false;
        activeMessage.editMessageEmbeds(getEmbed()).setActionRows(RAFFLE_BUTTONS_CLOSED).queue();
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder eb = new EmbedBuilder();

        if (prize.length()==0){
            eb.setTitle("Raffle!!!");
        } else {
            eb.setTitle("Raffle for " + prize);
        }
        eb.addField("Status",isOpen()?"Open to entries":"Closed to new entries",true);
        eb.addField("Entries",Integer.toString(getNumEntries()),true);

        return eb.build();
    }

    public void setActiveMessage(Message activeMessage) {
        if (this.activeMessage != null){
            this.activeMessage.delete().queue();
        }
        this.activeMessage=activeMessage;
        if(isOpen)
            this.activeMessage.editMessageEmbeds(getEmbed()).setActionRows(RAFFLE_BUTTONS_OPEN).queue();
        else
            this.activeMessage.editMessageEmbeds(getEmbed()).setActionRows(RAFFLE_BUTTONS_CLOSED).queue();
    }

    public void clearActiveMessage(){
        this.activeMessage.editMessageEmbeds(getEmbed()).setActionRows().queue();
        this.activeMessage=null;
    }

    public void updateActiveMessage() {
        activeMessage.editMessageEmbeds(getEmbed()).queue();
    }

    public enum EntryResult {
        added,closed,exists
    }

}
