package com.deguet.gutils.vote;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.deguet.gutils.graph.DGraph;
import com.deguet.gutils.graph.DGraphMatrix;
import com.deguet.gutils.graph.DGraphs;
import com.deguet.gutils.nuplets.Trio;

public class TidemanOnBallotBox<T> {

	private BallotBox<T> bbox;
	
	public TidemanOnBallotBox(BallotBox<T> bb){this.bbox = bb;}
	
	private Comparator<Trio<T,T,Long>> comparator = new Comparator<Trio<T,T,Long>>(){
		public int compare(Trio<T, T, Long> o1, Trio<T, T, Long> o2) {
			return o2.get3().compareTo(o1.get3());
		}
		
	};
	
	public RankedVote<T> results(){
		DGraph<T,Long> pairwise = bbox.computePairwise();
		// get the pairs sorted
		List<Trio<T,T,Long>> sorted = new ArrayList<Trio<T,T,Long>>();
		sorted.addAll( pairwise.triplets());
		sorted.sort(comparator);
		//for (Trio<T,T,Long> elt : sorted)System.out.println(elt);
		
		DGraph<T,Long> acyclic = new DGraphMatrix<T,Long>();
		for (Trio<T,T,Long> elt : sorted){
			DGraph<T,Long> candidate = acyclic.addEdge(elt.get1(), elt.get2(), elt.get3());
			if (!DGraphs.isCyclic(candidate)){
				acyclic = candidate;
			}
		}
		//System.out.println(DGraphs.toDot(acyclic, "a") );
		return RankedVote.fromListOfSet(DGraphs.topoSort(acyclic));
	}
	
}
