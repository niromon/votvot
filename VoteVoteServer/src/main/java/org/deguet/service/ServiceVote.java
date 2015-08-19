package org.deguet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import jersey.repackaged.com.google.common.collect.Lists;

import org.deguet.Services;
import org.deguet.model.civil.NQPerson;
import org.deguet.model.transfer.C2SVoteRequest;
import org.deguet.model.vote.NQAnswerReceipt;
import org.deguet.model.vote.NQQuestion;
import org.deguet.model.vote.NQQuestion.Status;
import org.deguet.model.vote.NQQuestionInOrOut;
import org.deguet.model.vote.NQQuestionInOrOut.Opinion;
import org.deguet.model.vote.NQResult;
import org.deguet.model.vote.NQAnswer;
import org.deguet.repo.RepoAnswerReceipt;
import org.deguet.repo.RepoQuestionInOrOut;
import org.deguet.service.ServiceSocial.NoToken;
import org.deguet.utils.CRUD;
import org.deguet.utils.FileRepository;
import org.deguet.utils.JPARepository;
import org.joda.time.DateTime;

public class ServiceVote {

	static CRUD<NQQuestion> repoPoll = new FileRepository<NQQuestion>(NQQuestion.class);
	static CRUD<NQAnswer> repoVote = new FileRepository<NQAnswer>(NQAnswer.class);
	static RepoQuestionInOrOut repoOpinion = new RepoQuestionInOrOut();
	static RepoAnswerReceipt repoHasVoted = new RepoAnswerReceipt();

	ServiceSocial ss = Services.social;
	
	public static class NoSuchVote extends Exception{}
	public static class NoSuchInOrOut extends Exception{}
	public static class NoSuchPoll extends Exception{}
	public static class AlreadyVoted extends Exception{}
	public static class AlreadyExpressedOpinion extends Exception{}
	public static class AlreadyAcceptedPoll extends Exception{}

	public List<NQQuestion> homePolls() {
		// integrate what's most voted, what's most recent, what's about to expire 
		// TODO
		return questionsByStatus(Status.Accepted);
	}
	
	public List<NQQuestion> propositions() {
		// integrate what's most voted, what's most recent, what's about to expire 
		// TODO
		return questionsByStatus(Status.Proposed);
	}
	
	public List<NQQuestion> questionsByStatus(Status s) {
		// integrate what's most voted, what's most recent, what's about to expire 
		// TODO
		List<NQQuestion> result = new ArrayList<NQQuestion>();
		for (NQQuestion p : repoPoll.getAll()){
			if (p.status == s) result.add(p);
		}
		return result;
	}

	public List<NQAnswerReceipt> votesBy(NQPerson p) {
		List<NQAnswerReceipt> res = new ArrayList<NQAnswerReceipt>();
		for (NQAnswerReceipt v : repoHasVoted.getAll()){
			if (v.voter.equals(p.getId())) res.add(v);
		}
		return res;
	}
	
	public List<NQAnswerReceipt> votesBy(NQPerson p, int year) {
		List<NQAnswerReceipt> res = new ArrayList<NQAnswerReceipt>();
		for (NQAnswerReceipt v : repoHasVoted.getAll()){
			if (v.voter.equals(p.getId())
					&& v.timestamp.getYear() == year) res.add(v);
		}
		return res;
	}

	public List<NQAnswer> votesBy(NQQuestion p) {
		List<NQAnswer> res = new ArrayList<NQAnswer>();
		for (NQAnswer v : repoVote.getAll()){
			if (v.question.getId().equals(p.getId())) res.add(v);
		}
		return res;
	}

	
	public List<NQQuestion> pollsForMyPosition(NQPerson user) {
		// TODO Auto-generated method stub
		return null;
	}

	public void create(NQQuestion p) {
		if (p.type == null) throw new IllegalArgumentException();
		repoPoll.save(p);
	}

	public NQQuestion pollById(UUID id) throws NoSuchPoll{
		NQQuestion p = repoPoll.get(id.toString());
		if (p == null) throw new NoSuchPoll();
		return p;
	}

	

	public List<NQAnswer> allVotes() {
		return repoVote.getAll();
	}

	public NQAnswerReceipt registerVote(C2SVoteRequest o, DateTime... forced) throws AlreadyVoted, NoToken{
		// validation
		try {
			
			NQAnswerReceipt existing = repoHasVoted.voteByFor(o.voterId, o.questionId);
			throw new AlreadyVoted();
		} catch (NoSuchVote e) {
			// Todo take the vote intention and make two things
			NQQuestion qq = repoPoll.get(o.questionId);;
			// - HasVoted with time and person who voted
			NQAnswerReceipt has = new NQAnswerReceipt();
			if (forced.length == 1) 
				has.timestamp = forced[0];
			else
				has.timestamp = DateTime.now();
			has.voter = o.voterId;
			has.questionId = qq.getId();
			repoHasVoted.save(has);
			// - Vote with the answer, the question and nothing else
			NQAnswer v = new NQAnswer();
			v.choice = o.choice;
			v.question = qq;
			if (v.question == null) throw new IllegalArgumentException("wrong poll id");
			repoVote.save(v); 
			return has;
		}
	}

	public NQResult resultsForPoll(String id) {
		NQResult res = new NQResult();
		NQQuestion poll = repoPoll.get(id);
		res.question = poll;
		res.result = new HashMap<String,Integer>();
		List<NQAnswer> votes = this.votesBy(poll);
		for (NQAnswer v : votes){
			if (res.result.get(v.choice) == null) res.result.put(v.choice, 1);
			else	res.result.put(v.choice, res.result.get(v.choice)+1);
		}
		return res;
	}
	
	public List<NQAnswerReceipt> particpationsForPoll(String id) {
		List<NQAnswerReceipt> res = Lists.newArrayList();
		System.out.println("Total has voted " + res.size());
		for (NQAnswerReceipt hs : repoHasVoted.getAll()){
			if (hs.questionId.equals(id))   res.add(hs);
		}
		return res;
	}

	public void deleteAll() {
		repoHasVoted.deleteAll();
		repoVote.deleteAll();
		repoPoll.deleteAll();
	}

	public void addPropositionOpinion(NQQuestionInOrOut opinion) throws AlreadyExpressedOpinion, AlreadyAcceptedPoll, NoSuchPoll{
		NQQuestion p = this.pollById(UUID.fromString(opinion.questionId));
		if (p.status.equals(Status.Accepted)) throw new AlreadyAcceptedPoll();
		
		try {
			NQQuestionInOrOut qioo = repoOpinion.inOrOutByFor(opinion.voterId, opinion.questionId);
			throw new AlreadyExpressedOpinion();
		} catch (NoSuchInOrOut e) {
			// this means no one express opinion, it is a go
		}
		
		repoOpinion.save(opinion);
		// this is when to eventually change status
		int countInclude = this.opinionsFor(p.getId(), Opinion.INCLUDE);
		int countExclude = this.opinionsFor(p.getId(), Opinion.EXCLUDE);
		int total = Services.social.countPeople();
		//System.out.println("Poll " + p.question);
		//System.out.println("Include " + countInclude + "  Exclude " + countExclude);
		//System.out.println("Total users " + total);
		if ((countInclude+countExclude) > 0.5 * total){
			if (countInclude > countExclude){
				p.status = Status.Accepted;
				p.acceptedDate = DateTime.now();
				repoPoll.save(p);
			}
			else{
				repoPoll.delete(p);
			}
		}
	}

	private int opinionsFor(String pollId, Opinion opinion) {
		int result = 0;
		for (NQQuestionInOrOut po : repoOpinion.getAll()){
			if (po.questionId.equals(pollId)){
				if (opinion.equals(po.opinion)) result++;
			}
		}
		return result;
	}

	public int numberOfPropositions() {
		int count = 0 ;
		for (NQQuestion p : repoPoll.getAll()){
			if (p.status == Status.Proposed) count++;
		}
		return count;
	}

	public List<NQQuestionInOrOut> supportsBy(NQPerson p) {
		List<NQQuestionInOrOut> result = new ArrayList<NQQuestionInOrOut>();
		for (NQQuestionInOrOut po : repoOpinion.getAll()){
			if (po.voterId.equals(p.getId())){
				result.add(po);
			}
		}
		return result;
	}

	



}
