package com.deguet.gutils.vote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.deguet.gutils.graph.DGraph;
import com.deguet.gutils.graph.export.SVGthroughGraphViz;
import com.deguet.gutils.random.CopiableRandom;


/**
 * see http://www.cs.wustl.edu/~legrand/rbvote/calc.html
 * for tests and reference
 * @author joris
 *
 */
public class TestShulze {

	@Test(timeout = 10000)
	public void testNeverFirstButWinsWithBallotBox(){
		BallotBox<String> bb = new BallotBox<String>();
		bb.add(RankedVote.fromCondense("A>B>C"));
		bb.add(RankedVote.fromCondense("C>B>D"));
		bb.add(RankedVote.fromCondense("D>B>A"));
		bb.add(RankedVote.fromCondense("E>B>D"));
		ShulzeOnBallotBox<String> shulze = new ShulzeOnBallotBox<String>(bb);
		System.out.println("Results " + shulze.results());
	}


	@Test(timeout = 10000)
	public void testShulze1(){
		BallotBox<String> bb = new BallotBox<String>();
		ShulzeOnBallotBox<String> shulze = new ShulzeOnBallotBox<String>(bb);
		for (int i = 0 ; i < 45 ; i++){
			RankedVote<String> vote = new RankedVote<String>()
					.addAtRank(1, "Paul")
					.addAtRank(5,"Peter")
					.addAtRank(5,"Billy");
			bb.add(vote);
		}
		for (int i = 0 ; i < 30 ; i++){
			RankedVote<String> vote = new RankedVote<String>()
					.addAtRank(1, "Peter").addAtRank(2,"Billy").addAtRank(5,"Paul");
			bb.add(vote);
		}
		for (int i = 0 ; i < 16 ; i++){
			RankedVote<String> vote = new RankedVote<String>()
					.addAtRank(1, "Billy").addAtRank(2,"Paul");
			bb.add(vote);
		}
		System.out.println("Results " + shulze.results());
		Assert.assertEquals("Billy", shulze.results().get(0).iterator().next());
	}

	/**
	 * when everyone votes the same, result shoud be that vote.
	 */
	@Test(timeout = 10000)
	public void testShulzeConsensus(){
		BallotBox<String> bb = new BallotBox<String>();
		ShulzeOnBallotBox<String> shulze = new ShulzeOnBallotBox<String>(bb);
		RankedVote<String> vote = new RankedVote<String>();
		vote.addAtRank(1, "Paul").addAtRank(5,"Peter").addAtRank(5,"Billy");
		for (int i = 0 ; i < 10 ; i++){
			bb.add(vote);
		}
		Assert.assertEquals("Result is the same as the unique vote", vote.asListOfSet(), shulze.results());
		//System.out.println("Results " + rs.results());
	}

	@Test(timeout = 10000)
	public void testShulzeConsensus2(){
		BallotBox<String> bb = new BallotBox<String>();
		RankedVote<String> vote = new RankedVote<String>().addAtRank(1, "Paul").addAtRank(4,"Peter").addAtRank(5,"Billy");
		bb.add(vote,10);
		ShulzeOnBallotBox<String> shulze = new ShulzeOnBallotBox<String>(bb);
		List<Set<String>> results = shulze.results();
		System.out.println("Results " + results);
		System.out.println("Vote " + vote+"  "+vote.asListOfSet()+" "+vote.toCondense());
		Assert.assertEquals("Result is the same as the unique vote", vote.asListOfSet(), results);

	}

	@Test( timeout = 3000)
	public void testShulzeWikipediaSmall(){
		testShulzeWikipedia(1);
	}


	@Test( timeout = 20000)
	public void testShulzeWikipediaBigger(){
		testShulzeWikipedia(1000);
	}

	public void testShulzeWikipedia(int amount){
		int mult = amount;
		BallotBox<String> bb = new BallotBox<String>();
		bb.add(RankedVote.fromCondense("A>C>B>E>D"), 5*mult);
		bb.add(RankedVote.fromCondense("A>D>E>C>B"), 5*mult);
		bb.add(RankedVote.fromCondense("B>E>D>A>C"), 8*mult);
		bb.add(RankedVote.fromCondense("C>A>B>E>D"), 3*mult);
		bb.add(RankedVote.fromCondense("C>A>E>B>D"), 7*mult);
		bb.add(RankedVote.fromCondense("C>B>A>D>E"), 2*mult);
		bb.add(RankedVote.fromCondense("D>C>E>B>A"), 7*mult);
		bb.add(RankedVote.fromCondense("E>B>A>D>C"), 8*mult);
		ShulzeOnBallotBox<String> shulze = new ShulzeOnBallotBox<String>(bb);
		List<Set<String>> r = shulze.results();
		List<Set<String>> expected = new ArrayList<Set<String>>();
		for (String s : (new String[]{"E","A","C","B","D"})){
			Set<String> element = new HashSet<String>();
			element.add(s);
			expected.add(element);
		}
		Assert.assertEquals("We know result should be E A C B D", r, expected);
		System.out.println("Results " + r);
	}


	//@Test
	public void testShulzeRandom() throws IOException{
		CopiableRandom r = new CopiableRandom(9875);
		String[] candidates = {"A","B","C","D","E","F","G","H","I","J"};
		for (int vote = 0 ; vote < 4 ; vote++){
			System.out.println("New One iteration " +vote  );
			BallotBox<String> bb = new BallotBox<String>();
			ShulzeOnBallotBox<String> shulze = new ShulzeOnBallotBox<String>(bb);
			System.out.println("Adding votes to " + shulze + " ...");
			for (int i = 0 ; i < 100000 ; i++){
				RankedVote<String> v = RankedVote.atRandom(r.asRandom(), candidates);
				bb.add(v);
			}
			System.out.println("Computing results ...");
			List<Set<String>> results = shulze.results();
		}
	}

	@Test
	public void testEqualityBetweenCandidates() throws IOException{
		BallotBox<String> bb = new BallotBox<String>();
		ShulzeOnBallotBox<String> shulze = new ShulzeOnBallotBox<String>(bb);
		String[] candidates = {"A","B","C","D","E"};
		for (int i = 0 ; i < candidates.length ; i++){
			RankedVote<String> v = new RankedVote<String>();
			v.addAtRank(1, candidates[i]);
			bb.add(v);
		}
		System.out.println("Computing results ...");
		List<Set<String>> results = shulze.results();
		System.out.println("Results " + results);
	}



}
