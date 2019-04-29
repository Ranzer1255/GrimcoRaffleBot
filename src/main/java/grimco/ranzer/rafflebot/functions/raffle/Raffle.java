package grimco.ranzer.rafflebot.functions.raffle;

import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Raffle {

    private boolean isOpen;

    private final List<Member> entries = new ArrayList<>();

    public Raffle(){
        isOpen = true;
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
     * @return true if entrant was present in th elist
     */
    public boolean removeEntry(Member entrant){
        return entries.remove(entrant);
    }

    /**
     * clears the List of all entrants
     */
    public void clearEntries(){
        entries.clear();
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
    }

    public enum EntryResult {
        added,closed,exists
    }


}
