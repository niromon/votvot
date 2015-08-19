package org.deguet.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.deguet.model.civil.NQPerson;
import org.deguet.utils.JPARepository;

public class RepoPerson extends JPARepository<NQPerson>{

	public RepoPerson() {
		super(NQPerson.class);
	}

	public NQPerson findByEmail(String email) {
		EntityManager em = getEntityManager();
		String query = "SELECT p FROM NQPerson p WHERE p.email = :email";
		Query q = em.createQuery(query);
		q.setParameter("email", email);
		List<NQPerson> result = q.getResultList();
		if (result.size() == 1) return result.get(0);
		if (result.size() == 0) return null;
		throw new IllegalArgumentException();
	}

}
