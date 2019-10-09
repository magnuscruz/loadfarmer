package pt.uc.greenhub.springbatch.dao;

import org.springframework.stereotype.Repository;

import pt.uc.greenhub.springbatch.web.controller.CpuDTO;

@Repository
public class CpuDAO extends AbstractJpaDAO<CpuDTO> {

	public CpuDAO() {
		setClazz(CpuDTO.class);
	}
}
