package net.petrikainulainen.springbatch.dao;

import java.io.Serializable;
import java.util.List;

public interface JpaDAO<T extends Serializable> {

	T findOne(long id);

	List<T> findAll();

	void create(T entity);

	T update(T entity);

	void delete(T entity);

	void deleteById(long entityId);

	List<T> findAll(int pageSize, int index);

}