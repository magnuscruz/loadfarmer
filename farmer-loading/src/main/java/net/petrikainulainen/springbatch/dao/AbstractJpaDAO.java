package net.petrikainulainen.springbatch.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

public abstract class AbstractJpaDAO<T extends Serializable> implements JpaDAO<T> {

	private Class<T> clazz;

	@PersistenceContext
	EntityManager entityManager;

	public final void setClazz(Class<T> clazzToSet) {
		this.clazz = clazzToSet;
	}

	@Override
	public T findOne(long id) {
		return entityManager.find(clazz, id);
	}

	@Override
	public List<T> findAll() {
		return entityManager.createQuery("from " + clazz.getName(), clazz).getResultList();
	}
	
	@Override
	public List<T> findAll(int pageSize, int index) {
		TypedQuery<T> query = entityManager.createQuery("from " + clazz.getName(), clazz);
		query.setFirstResult(index);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	@Override
	public void create(T entity) {
		entityManager.persist(entity);
	}

	@Override
	public T update(T entity) {
		return entityManager.merge(entity);
	}

	@Override
	public void delete(T entity) {
		entityManager.remove(entity);
	}

	@Override
	public void deleteById(long entityId) {
		T entity = findOne(entityId);
		delete(entity);
	}
}