package com.deguet.gutils.vote;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.deguet.gutils.graph.DGraph;
import com.deguet.gutils.graph.DGraphMatrix;
import com.deguet.gutils.string.Blanks;

/**
 * Compiles ballot without keeping all of them.
 * Count the same ballot to have a compact representation.
 * @author joris
 *
 */
public class BallotBox<T> {
	
	private final Map<RankedVote<T>,Integer> votes = new HashMap<RankedVote<T>,Integer>();
	
	// cached graph
	private DGraph<T,Long> pairwise;
	
	public void add(RankedVote<T> vote, Integer qty){
		if (votes.containsKey(vote)){
			votes.put(vote, votes.get(vote)+qty);
		}else{
			votes.put(vote, qty);
		}
		pairwise = null;			// invalidate the cache
	}
	
	public void add(RankedVote<T> vote){
		add(vote,1);
	}
	
	public BallotBox<T> without(T... candidates){
		BallotBox<T> result = new BallotBox<T>();
		for (RankedVote<T> v : votes.keySet()){
			int number = votes.get(v);
			RankedVote<T> without = v.without(candidates);
			//System.out.println(without.toCondense() +"   " + v);
			result.add(without, number);
		}
		return result;
	}
	
	/**
	 * compute results as if only the first vote was taken into account.
	 * if the rankedvote contains a tie, we split into 1/n where n the nimber of tied candidates
	 * @return
	 */
	public Map<T,Double> nonPreferentialResults(){
		double total = 0.0;
		Map<T, Double> result = new HashMap<T,Double>();
		for (T t : candidates()){
			result.put(t, 0.0);
		}
		for (RankedVote<T> vote : votes.keySet()){
			Set<T> firsts = vote.asListOfSet().get(0);
			for (T first : firsts){
				double part = 1.0*votes.get(vote)/firsts.size();
				result.put(first, result.get(first) + part);
				total += part;
			}
		}
		result.put(null, total);
		System.out.println("Total " + total);
		return result;
	}
	
	/**
	 * returns the set of undisputedWinners
	 * @return
	 */
	public Set<T> undisputedWinners() {
		return this.computePairwise().sources();
	}
	
	
	public Set<T> candidates(){
		Set<T> result = new HashSet<T>();
		for (RankedVote<T> vote : votes.keySet()){
			result.addAll(vote.candidates());
		}
		return result;
	}
	
	/**
	 * Computes the pairwise graph for these ballots
	 * @return
	 */
	public DGraph<T,Long> computePairwise(){
		if (pairwise != null) return pairwise;
		DGraph<T,Long> res = new DGraphMatrix<T,Long>();
		Set<T> cands = this.candidates();
		for (T a : cands){
			for (T b : cands){
				if (a != b){
					long score = 0;
					// go through all votes for this pair of candidates
					for (RankedVote<T> vote: votes.keySet()){
						int s = vote.score(a, b);
						//System.out.println(vote + "  gives  "+s );
						if (s > 0) score+=s*votes.get(vote); 
					}
					res = res.addEdge(a, b, score);
					//System.out.println("============================= Score for "+cands[i]+" against "+cands[j]+"  = " +score);
				}
			}
		}
		pairwise = deleteSmallerEdge(res);
		return pairwise;
	}
	
	private static <T>  DGraph<T, Long> deleteSmallerEdge(DGraph<T, Long> g) {
		T[] vs = (T[]) g.vertices().toArray();
		for (int i = 0 ; i < vs.length ; i++){
			for (int j = i+1 ; j < vs.length ; j++){
				T a = vs[i];
				T b = vs[j];
				Long ab = g.getEdge(a, b);
				Long ba = g.getEdge(b, a);
				if (ab != null && ba != null){
					if (ab == ba){
						g = g.delEdge(b, a).delEdge(a, b);
					}
					if (ab>ba){
						g = g.delEdge(b, a);
					}
					else{
						g = g.delEdge(a, b);
					}
				}
			}
		}
		return g;
	}
	
	public String toString(){
		return this.stringMatrix(10);
	}
	
	/**
	 * Gives a String representation of the pairwise graph
	 * @param columnWidth
	 * @return
	 */
	public String stringMatrix(int columnWidth) {
		return Condorcet.stringMatrix(computePairwise(), columnWidth);
	}
	
}
