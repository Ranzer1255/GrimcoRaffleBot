package grimco.ranzer.rafflebot.functions.levels;

import grimco.ranzer.rafflebot.data.IMemberData;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public class UserLevel implements Comparable<UserLevel> {

	final private int experience;
	final private Member member;
	final private long lastXP;
	
	/**
	 * create new UserLevel with initial Expereience
	 * @param XP
	 */
	public UserLevel(Member m, int XP, Long lxp) {
		experience = XP;
		member=m;
		lastXP = lxp;
	}
	
	public UserLevel(Member m, int XP) {
		experience = XP;
		member=m;
		lastXP = System.currentTimeMillis();
	}
	

	public int getXP() {
		return experience;
	}

	public int getLevel() {
		return getLevel(getXP());
	}
	
	public long getLastXPTime() {
		return lastXP;
	}

	public Member getMember(){
		return member;
	}
	
	public User getUser() {
		return member.getUser();
	}

	public Guild getGuild() {
		return member.getGuild();
	}

	public static int getLevel(int xp){
		int rtn = 1;
		boolean found=false;
		while(!found){
			rtn+=1;
			if (xp<calcXPForLevel(rtn)){
				found=true;
			}
			
		}
		return rtn-1;
	}
	
	static public int calcXPForLevel(int level){
				
		return (int)Math.floor((500*Math.pow(level,2))-(500*level));
		
	}

	@Override
	public int compareTo(UserLevel o) {
		
		if(this.getXP()<o.getXP())
			return 1;
		if(this.getXP()>o.getXP())
			return -1;
		return 0;
		
	}
}
