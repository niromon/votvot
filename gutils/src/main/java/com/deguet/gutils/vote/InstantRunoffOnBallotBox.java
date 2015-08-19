package com.deguet.gutils.vote;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InstantRunoffOnBallotBox<T> {

	private BallotBox<T> bbox;
	
	public InstantRunoffOnBallotBox(BallotBox<T> bb){
		this.bbox = bb;
	}
	
	public boolean hasMajority(){
		Map<T, Double> simples = bbox.nonPreferentialResults();
		double total = simples.get(null);
		System.out.println("Majority " + total);
		for (T t : simples.keySet()){
			if (simples.get(t) > total / 2) return true;
		}
		return false;
	}
	
	// compute the worst candidates based on first choices to eliminate them
	public Set<T> worst(BallotBox<T> bb){
		Set<T> result = new HashSet<T>();
		Double worst = Double.POSITIVE_INFINITY;
		Map<T, Double> simples = bb.nonPreferentialResults();
		for (T t : simples.keySet()){
			if (t == null) continue;
			if (simples.get(t) < worst) {
				result.clear();
				result.add(t);
				worst = simples.get(t);
			} else if(simples.get(t).equals(worst)){
				result.add(t);
			}
		}
		return result;
	}
	
	public RankedVote<T> results(){
		// we systematically remove the 
		int rank = bbox.candidates().size();
		BallotBox<T> iterate = bbox;
		RankedVote<T> result  = new RankedVote<T>();
		while(iterate.candidates().size() > 0){
			// find the last candidates
			Set<T> worsts = this.worst(iterate);
			// add the at a bad rank
			for (T t: worsts){
				result = result.addAtRank(iterate.candidates().size()+1, t);
			}
			
			iterate = iterate.without((T[]) worsts.toArray());
			//System.out.println("==============================");
			//System.out.println(iterate.stringMatrix(10));
		}
		
		return result;
	}
	
}
