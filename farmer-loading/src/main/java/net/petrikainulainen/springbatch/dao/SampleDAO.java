package net.petrikainulainen.springbatch.dao;

import org.springframework.stereotype.Repository;

import net.petrikainulainen.springbatch.sample.SampleDTO;

@Repository
public class SampleDAO extends AbstractJpaDAO<SampleDTO> {

	public SampleDAO() {
		setClazz(SampleDTO.class);
	}
}
