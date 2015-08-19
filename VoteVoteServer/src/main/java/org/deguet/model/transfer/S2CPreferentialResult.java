package org.deguet.model.transfer;

import java.util.Set;

import com.deguet.gutils.nuplets.Trio;
import com.deguet.gutils.vote.RankedVote;

public class S2CPreferentialResult {

	public String undisputedWinner;
	
	public RankedVote<String> shulze;			// Shulze Method
	public RankedVote<String> tideman;			// Tideman Ranked Pairs
	public RankedVote<String> instant;			// Instant Runoff
	
	public Set<Trio<String,String,Long>> edges;

	
	
}
