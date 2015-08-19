package org.deguet.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.deguet.model.vote.NQQuestionInOrOut;
import org.deguet.service.ServiceVote.NoSuchInOrOut;
import org.deguet.service.ServiceVote.NoSuchVote;
import org.deguet.utils.JPARepository;

public class RepoQuestionInOrOut extends JPARepository<NQQuestionInOrOut>{

	public RepoQuestionInOrOut() {
		super(NQQuestionInOrOut.class);
	}
	
	public NQQuestionInOrOut inOrOutByFor(String personId, String pollId) throws NoSuchInOrOut{
		EntityManager em = getEntityManager();
		String query = "SELECT h FROM NQQuestionInOrOut h WHERE h.voterId = :voterId AND h.questionId = :pollId";
		Query q = em.createQuery(query);
		q.setParameter("voterId", personId);
		q.setParameter("pollId", pollId);
		List<NQQuestionInOrOut> result = q.getResultList();
		if (result.size() == 1) return result.get(0);
		if (result.size() == 0) throw new NoSuchInOrOut();
		throw new IllegalArgumentException();
		
	}

	
	
}
