package com.deguet.gutils.vote;

import java.util.ArrayList;
import java.util.List;

import com.deguet.gutils.graph.DGraph;
import com.deguet.gutils.graph.DGraphs;
import com.deguet.gutils.random.CopiableRandom;

import org.junit.Test;

public class TestCondorcet {

	@Test(timeout = 10000)
	public void testCondorcetToString(){
		List<RankedVote<String>> list = new ArrayList<RankedVote<String>>();
		RankedVote<String> vote = new RankedVote<String>();
		vote.addAtRank(1, "Paul").addAtRank(5,"Peter").addAtRank(5,"Billy");
		for (int i = 0 ; i < 100 ; i++){
			list.add(vote);
		}
		DGraph<String, Long> pairwise = Condorcet.computePairwise(list);
		System.out.println(DGraphs.toDot(pairwise, "bla"));
		String tested = Condorcet.stringMatrix(pairwise, 15);
		System.out.println("Results \n" + tested);
	}
	
	@Test(timeout = 10000)
	public void testCondorcetToString2(){
		List<RankedVote<String>> list = new ArrayList<RankedVote<String>>();
		CopiableRandom r = new CopiableRandom(9875);
		String[] candidates = {"A","B","C","D","E","F","G","H","I","J"};
		for (int i = 0 ; i < 100 ; i++){
			RankedVote<String> vote = RankedVote.atRandom(r.asRandom(),candidates);
			list.add(vote);
		}
		DGraph<String, Long> pairwise = Condorcet.computePairwise(list);
		System.out.println(DGraphs.toDot(pairwise, "bla"));
		String tested = Condorcet.stringMatrix(pairwise, 7);
		System.out.println("Results \n" + tested);
	}
	
}
