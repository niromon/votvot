package com.deguet.gutils.vote;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.deguet.gutils.graph.DGraph;
import com.deguet.gutils.graph.DGraphMatrix;
import com.deguet.gutils.string.Blanks;

public class Condorcet {

	public static class NoCondorcetWinner extends Exception {}
	
	public static <T>  T undisputed(DGraph<T, Long> g) throws NoCondorcetWinner {
		Set<T> sources = g.sources();
		if (sources.size() == 1) return g.sources().iterator().next();
		throw new NoCondorcetWinner();
	}

	public static <T>  String stringMatrix(DGraph<T, Long> pairwise, int columnWidth) {
		StringBuilder sb = new StringBuilder();
		Set<T> candidates = pairwise.vertices();
		int size = columnWidth;
		// first line
		{
			sb.append(Blanks.nth((size+1)*(candidates.size()+1)+1, "-")+"\n");
			sb.append("|");
			sb.append(String.format("%1$-" + size + "s|", "\\"));
			for (T c : candidates){
				sb.append(String.format("%1$-" + size + "s|", c.toString()));
			}
			sb.append("\n");
			sb.append(Blanks.nth((size+1)*(candidates.size()+1)+1, "-")+"\n");
		}
		for (T a : candidates){
			sb.append("|");
			sb.append(String.format("%1$-" + size + "s|", a.toString()));
			for (T b : candidates){
				if (a!=b){
					Long ab = pairwise.getEdge(a, b);
					Long ba = pairwise.getEdge(b, a);
					if (ab != null){
						sb.append(String.format("%1$-" + size + "s|", ab));
					}else if (ba != null){
						sb.append(String.format("%1$-" + size + "s|", "("+ba+")"));
					}
					else{
						sb.append(String.format("%1$-" + size + "s|", " "));
					}
					
				}
					else{
					sb.append(String.format("%1$-" + size + "s|", ""));
					}
			}
			sb.append("\n");
			sb.append(Blanks.nth((size+1)*(candidates.size()+1)+1, "-")+"\n");
		}
		//sb.append("\n");
		return sb.toString();
	}

	public static <T> DGraph<T,Long> computePairwise(List<RankedVote<T>> votes){
		Set<T> candidates = new HashSet<T>();
		for (RankedVote<T> vote: votes){
			candidates.addAll(vote.candidates());
		}
		DGraph<T,Long> res = new DGraphMatrix<T,Long>();
		T[] cands = (T[]) candidates.toArray();
		for (int i = 0 ; i < cands.length ; i++){
			for (int j = 0 ; j < cands.length ; j++){
				if (i != j){
					long score = 0;
					T a = cands[i];
					T b = cands[j];
					for (RankedVote<T> vote: votes){
						int s = vote.score(a, b);
						//System.out.println(vote + "  gives  "+s );
						if (s > 0) score+=s; 
					}
					res = res.addEdge(a, b, score);
					//System.out.println("============================= Score for "+cands[i]+" against "+cands[j]+"  = " +score);
				}
			}
		}
		res = deleteSmallerEdge(res);
		return res;
	}


	public static <T>  DGraph<T, Long> deleteSmallerEdge(DGraph<T, Long> g) {
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

}
