package com.deguet.gutils.vote;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.deguet.gutils.graph.DGraph;
import com.deguet.gutils.graph.DGraphs;
import com.deguet.gutils.random.CopiableRandom;

public class TestInstantRunoff {

	@Test//(timeout = 10000)
	public void testCondorcetToString(){
		BallotBox<String> bb = new BallotBox<String>();
		CopiableRandom r = new CopiableRandom(9875);
		for (int i = 0 ; i < 10 ; i++){
			RankedVote<String> vote = //RankedVote.atRandom(r.asRandom(),candidates);
					new RankedVote<String>().addAtRank(1, "Paul").addAtRank(5,"Peter").addAtRank(5,"Billy");
			bb.add(vote);
		}
		InstantRunoffOnBallotBox<String> instant = new InstantRunoffOnBallotBox<String>(bb);
		System.out.println(instant.hasMajority());
		System.out.println(instant.results().toCondense());
	}
	
}
