package com.deguet.gutils.vote;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.deguet.gutils.graph.DGraph;
import com.deguet.gutils.graph.DGraphs;

public class ShulzeOnBallotBox<T> {

	private BallotBox<T> bbox;
	
	public ShulzeOnBallotBox(BallotBox<T> bb){
		this.bbox = bb;
	}
	
	
	/**
	 * Return the result as the ordered list of winners.
	 * First element is the winner.
	 * @return
	 */
	public List<Set<T>> results(){
		// go through all votes to determine the candidates
		Set<T> candidates = bbox.candidates();
		//System.out.println("Candidates "+ candidates +" votes "+votes.size());
		// compute pairwise scores and display the graph
		DGraph<T,Long> pairwise = bbox.computePairwise();
		// compute strongest paths when there is no undisputed winner
		DGraph<T,Long> strongest = computeStrongest(pairwise, candidates);
		//System.out.println(DGraphs.toDot(strongest,"strongest"));
		Set<T> winners = strongest.sources();
		if (!winners.isEmpty()){
			for (T w : winners)
				System.out.println(w + " is a winner");
		}
		// compute the order from the paths is actually a topological sort on the graph
		List<Set<T>> result = DGraphs.topoSort(strongest);
		// there is a cycle in the strongest graph but we still might have winners.
		if (result == null) {
			Set<T> sources = strongest.sources();
			if (sources.isEmpty()) throw new IllegalArgumentException("Tie");
			else{
				result = new ArrayList<Set<T>>();
				result.add(sources);
				return result;
			}
		}
		else{
			return result;
		}
	}
	
	public static <T> DGraph<T,Long> computeStrongest(DGraph<T,Long> pairwise, Set<T> cs){
		DGraph<T,Long> strong = pairwise;
		T[] cands = (T[]) cs.toArray();
		for (int a = 0 ; a < cands.length ; a++){
			for (int b = 0 ; b < cands.length ; b++){
				if (a != b){
					for (int c = 0 ; c < cands.length ; c++){
						if (a != c && b != c){
							Long atoc = strong.getEdge(cands[a],cands[c]);
							atoc = atoc==null?0:atoc;
							Long ctob = strong.getEdge(cands[c],cands[b]);
							ctob = ctob==null?0:ctob;

							Long atob = strong.getEdge(cands[a],cands[b]);
							atob = atob==null?0:atob;
							long max = Math.max(atob, Math.min(atoc,ctob));
							if (max >0)
								strong = strong.replaceEdge(cands[a], cands[b], max);
						}
					}
				}
			}
		}
		strong = Condorcet.deleteSmallerEdge(strong);
		return strong;
	}

	
}
