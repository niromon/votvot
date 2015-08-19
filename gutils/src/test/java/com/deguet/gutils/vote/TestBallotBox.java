package com.deguet.gutils.vote;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.deguet.gutils.graph.DGraph;
import com.deguet.gutils.graph.DGraphs;
import com.deguet.gutils.random.CopiableRandom;

public class TestBallotBox {

	@Test(timeout = 10000)
	public void testBallotBoxToString(){
		BallotBox<String> bb = new BallotBox<String>();
		RankedVote<String> vote = new RankedVote<String>().addAtRank(1, "Paul").addAtRank(5,"Peter").addAtRank(5,"Billy");
		for (int i = 0 ; i < 100 ; i++){
			bb.add(vote);
		}
		DGraph<String, Long> pairwise = bb.computePairwise();
		System.out.println(DGraphs.toDot(pairwise, "bla"));
		String tested = bb.stringMatrix(15);
		System.out.println("Results \n" + tested);
	}
	
	
	@Test(timeout = 10000)
	public void testCondorcetVersusBallotBox(){
		BallotBox<String> bb = new BallotBox<String>();
		List<RankedVote<String>> list = new ArrayList<RankedVote<String>>();
		
		// add the same votes to the Condorcet List and BallotBox
		CopiableRandom r = new CopiableRandom(9875);
		String[] candidates = {"A","B","C","D","E","F","G","H","I","J"};
		for (int i = 0 ; i < 100 ; i++){
			RankedVote<String> vote = RankedVote.atRandom(r.asRandom(),candidates);
			list.add(vote);
			bb.add(vote);
		}
		DGraph<String, Long> pairwisect = Condorcet.computePairwise(list);
		DGraph<String, Long> pairwisebb = bb.computePairwise();
		String condorcet = Condorcet.stringMatrix(pairwisect, 7);
		String ballotbox = bb.stringMatrix(7);
		System.out.println(ballotbox);
		System.out.println(condorcet);
		Assert.assertEquals(condorcet, ballotbox);
	}
	
	@Test	//(timeout = 10000)
	public void testCondorcetAndNonPreferentialResult(){
		BallotBox<String> bb = new BallotBox<String>();
		List<RankedVote<String>> list = new ArrayList<RankedVote<String>>();
		
		// add the same votes to the Condorcet List and BallotBox
		CopiableRandom r = new CopiableRandom(9875);
		String[] candidates = {"A","B","C"};
		for (int i = 0 ; i < 10 ; i++){
			RankedVote<String> vote = //RankedVote.atRandom(r.asRandom(),candidates);
					new RankedVote<String>().addAtRank(1, "Paul").addAtRank(5,"Peter").addAtRank(5,"Billy");
			list.add(vote);
			bb.add(vote);
		}
		System.out.println("Non Pref " + bb.nonPreferentialResults());
		System.out.println("undisputed " + bb.undisputedWinners());
	}
	
}
