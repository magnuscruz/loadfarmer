package pt.uc.greenhub.springbatch.dao;

import org.springframework.stereotype.Repository;

import pt.uc.greenhub.springbatch.web.controller.SampleDTO;

@Repository
public class SampleDAO extends AbstractJpaDAO<SampleDTO> {

	public SampleDAO() {
		setClazz(SampleDTO.class);
	}
}
