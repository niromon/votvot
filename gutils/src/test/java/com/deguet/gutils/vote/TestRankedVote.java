package com.deguet.gutils.vote;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.deguet.gutils.vote.*;

public class TestRankedVote {

	@Test(timeout = 12000)
	public void testVoteCodec(){
		String[] candidates = {"Paul", "Joris", "Evariste", "Malcolm", "Alexandre"};
		Random r = new Random(653267);
		for (int i = 0 ; i < 1000 ; i++){
			RankedVote<String> vote = RankedVote.atRandom(r, candidates);
			Long encoded = vote.encode(candidates);
			System.out.println("Encoded " + encoded + "  vote  "+ vote);
			RankedVote<String> decoded  = RankedVote.decode(encoded, candidates);
			Assert.assertEquals("Même vote ", decoded, vote);
		}
	}
	
	@Test(timeout = 12000, expected = IllegalArgumentException.class)
	public void testWrongRank(){
		RankedVote<Integer> vote = new RankedVote<Integer>();
		vote.addAtRank(0, 99);
	}
	
	@Test(timeout = 12000, expected = IllegalArgumentException.class)
	public void testDoubleInsertion(){
		RankedVote<Integer> vote = new RankedVote<Integer>();
		vote.addAtRank(1, 99);
		vote.addAtRank(3, 99);
	}
	
	@Test(timeout = 12000, expected = IllegalArgumentException.class)
	public void testCondenseError(){
		RankedVote<String> vote = new RankedVote<String>();
		vote.addAtRank(1, "pipi>");
		vote.toCondense();
	}
	
	@Test(timeout = 12000)
	public void testVoteCondensate(){
		String[] candidates = {"Joris", "Evariste", "Malcolm", "Alexandre"};
		Random r = new Random(653267);
		for (int i = 0 ; i < 5 ; i++){
			RankedVote<String> vote = RankedVote.atRandom(r, candidates);
			System.out.println("==============================================");
			System.out.println(vote.toCondense());
			System.out.println(vote);
		}
	}
	
	@Test(timeout = 12000)
	public void testVoteFromCondensate(){
		List<RankedVote<String>> votes = RankedVote.fromCondenseList("55|a>b=c\n99|c>b>a");
		System.out.println("Votes " + votes.size()+" "+votes);
		RankedVote<String> vote = RankedVote.fromCondense("a>b=c");
		System.out.println("Vote " + vote);
	}
	
	@Test(timeout = 12000)
	public void testSetHashEquals(){
		Set<RankedVote<String>> set = new HashSet<RankedVote<String>>();
		List<RankedVote<String>> list = new ArrayList<RankedVote<String>>();
		for (int i = 0 ; i < 100 ; i++){
			List<RankedVote<String>> votes = RankedVote.fromCondenseList("10|a>b=c\n5|c>b>a");
			set.addAll(votes);
			list.addAll(votes);
		}
		System.out.println("Set i " +set);
		Assert.assertEquals("Set ", set.size() , 2);
		Assert.assertEquals("List ", list.size() , 100*15);
	}
	
	/**
	 * this test shows that this encoding is too slow
	 */
	//@Test(timeout = 12000)
	public void testVoteCodec2(){
		String[] candidates = {"Peter", "Marcel", "Joris", "Evariste", "Malcolm", "Alexandre"};
		Random r = new Random(653267);
		for (int i = 0 ; i < 1000 ; i++){
			RankedVote<String> vote = RankedVote.atRandom(r, candidates);
			Long encoded = vote.encode(candidates);
			//System.out.println("Encoded " + encoded + "  vote  "+ vote);
			RankedVote<String> decoded  = RankedVote.decode(encoded, candidates);
			Assert.assertEquals("Même vote ", decoded, vote);
		}
	}
	
	@Test(timeout = 12000)
	public void testVoteCodec2Condensate(){
		String[] candidates = {"Peter", "Marcel", "Joris", "Evariste", "Malcolm", "Alexandre"};
		Random r = new Random(653267);
		for (int i = 0 ; i < 1000 ; i++){
			RankedVote<String> vote = RankedVote.atRandom(r, candidates);
			String s = vote.toCondense();
			RankedVote<String> decoded  = RankedVote.fromCondense(s);
			String s2 = decoded.toCondense();
			RankedVote<String> decoded2  = RankedVote.fromCondense(s2);
			Assert.assertEquals("Même vote ", decoded2, decoded);
		}
	}
	
	
	@Test(timeout = 10000)
	public void testVoteAtRandom(){
		String[] candidates = {"Paul", "Bob", "John", "Peter", "Marcel", "Joris", "Evariste", "Malcolm", "Alexandre"};
		Random r = new Random(653267);
		for (int i = 0 ; i < 100 ; i++){
			RankedVote<String> vote = RankedVote.atRandom(r, candidates);
			System.out.println(vote);
		}
	}
	
	@Test(timeout = 100)
	public void testAsListofSet(){
		RankedVote<String> vote = RankedVote.fromCondense("A>C=D>E");
		List<Set<String>> listset = vote.asListOfSet();
		RankedVote<String> recov = RankedVote.fromListOfSet(listset);
		System.out.println(vote.toCondense()+"  "+ recov.toCondense());
	}
	
	@Test(timeout = 100)
	public void testAsListofSetCanonicalForm(){
		RankedVote<String> vote = new RankedVote<String>().addAtRank(3, "A","B").addAtRank(1, "D");
		List<Set<String>> listset = vote.asListOfSet();
		RankedVote<String> recov = RankedVote.fromListOfSet(listset);
		System.out.println(vote.toCondense()+"  "+ recov.toCondense());
		System.out.println(vote+"  "+ recov);
	}

}
