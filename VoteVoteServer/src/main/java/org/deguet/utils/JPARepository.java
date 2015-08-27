package org.deguet.utils;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import org.deguet.model.Identifiable;

public class JPARepository<T extends Identifiable> implements CRUD<T> {

	private static EntityManagerFactory emf;
	protected EntityManager getEntityManager() {
		if (emf == null){
			emf = Persistence.createEntityManagerFactory("hsql");
			//emf = Persistence.createEntityManagerFactory("derby");
		}
		return emf.createEntityManager();
	}

	Class<T> entityClass;
	
	public JPARepository(Class<T> classs){
		this.entityClass = classs;
	}

	public T get(String id) {
		return getEntityManager().find(entityClass, id);
	}

	public List<T> getAll() {
		javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}

	public void save(T a) throws org.deguet.utils.CRUD.BadId {
		//System.out.println("Hibernate save " + a);
		EntityManager em = getEntityManager();
		if (a.getId() == null){
			a.setId(UUID.randomUUID().toString());
			em.getTransaction().begin();
			em.persist(a);
			em.getTransaction().commit();
		}
		else{
			em.getTransaction().begin();
			em.merge(a);
			em.getTransaction().commit();
		}
	}

	public void delete(T a) {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		T toDelete = em.merge(a);
		em.remove(toDelete);
		em.flush();
		em.getTransaction().commit();
	}

	public void deleteAll() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		for (T t : this.getAll()){
			T toDelete = em.merge(t);
			em.remove(toDelete);
		}
		em.getTransaction().commit();
	}

	public int count() {
		javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
		cq.select(getEntityManager().getCriteriaBuilder().count(rt));
		javax.persistence.Query q = getEntityManager().createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}

}
