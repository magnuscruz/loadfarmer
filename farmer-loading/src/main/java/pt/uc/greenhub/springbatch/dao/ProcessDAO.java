package pt.uc.greenhub.springbatch.dao;

import org.springframework.stereotype.Repository;

import pt.uc.greenhub.springbatch.web.controller.ProcessDTO;

@Repository
public class ProcessDAO extends AbstractJpaDAO<ProcessDTO> {

	public ProcessDAO() {
		setClazz(ProcessDTO.class);
	}
}
