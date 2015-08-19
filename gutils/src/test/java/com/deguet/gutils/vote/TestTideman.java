package com.deguet.gutils.vote;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class TestTideman {

	@Test(timeout = 10000)
	public void testNeverFirstButWinsWithBallotBox(){
		BallotBox<String> bb = new BallotBox<String>();
		bb.add(RankedVote.fromCondense("A>B>C"));
		bb.add(RankedVote.fromCondense("C>B>D"));
		bb.add(RankedVote.fromCondense("D>B>A"));
		bb.add(RankedVote.fromCondense("E>B>D"));
		TidemanOnBallotBox<String> tideman = new TidemanOnBallotBox<String>(bb);
		System.out.println("Results " + tideman.results());
	}
	
	@Test(timeout = 10000)
	public void testNeverFirstButWinsWithBallotBox2(){
		BallotBox<String> bb = new BallotBox<String>();
		bb.add(RankedVote.fromCondense("A>B>C"),7);
		bb.add(RankedVote.fromCondense("C>B>D"),13);
		bb.add(RankedVote.fromCondense("D>B>A"),44);
		bb.add(RankedVote.fromCondense("E>B>D"),3);
		TidemanOnBallotBox<String> tideman = new TidemanOnBallotBox<String>(bb);
		System.out.println("Results " + tideman.results());
	}
	
	@Test(timeout = 10000)
	public void testTidemanConsensus2(){
		BallotBox<String> bb = new BallotBox<String>();
		RankedVote<String> vote = new RankedVote<String>().addAtRank(1, "Paul").addAtRank(4,"Peter").addAtRank(5,"Billy");
		for (int i = 0 ; i < 10 ; i++){
			bb.add(vote);
		}
		TidemanOnBallotBox<String> tideman = new TidemanOnBallotBox<String>(bb);
		System.out.println("Results " + tideman.results());
		Assert.assertEquals("Result is the same as the unique vote", vote.toCondense(), tideman.results().toCondense());
		
	}
	
}
