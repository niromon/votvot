package org.deguet.repo;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.deguet.model.civil.NQPerson;
import org.deguet.model.vote.NQAnswerReceipt;
import org.deguet.service.ServiceVote.NoSuchVote;
import org.deguet.utils.JPARepository;

public class RepoAnswerReceipt extends JPARepository<NQAnswerReceipt>{

	public RepoAnswerReceipt() {
		super(NQAnswerReceipt.class);
	}
	
	public NQAnswerReceipt voteByFor(String personId, String pollId) throws NoSuchVote{
		EntityManager em = getEntityManager();
		String query = "SELECT h FROM NQAnswerReceipt h WHERE h.voter = :voterId AND h.questionId = :pollId";
		Query q = em.createQuery(query);
		q.setParameter("voterId", personId);
		q.setParameter("pollId", pollId);
		List<NQAnswerReceipt> result = q.getResultList();
		if (result.size() == 1) return result.get(0);
		if (result.size() == 0) throw new NoSuchVote();
		throw new IllegalArgumentException();
		
	}

}
