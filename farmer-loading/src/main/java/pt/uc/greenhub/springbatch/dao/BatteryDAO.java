package pt.uc.greenhub.springbatch.dao;

import org.springframework.stereotype.Repository;

import pt.uc.greenhub.springbatch.web.controller.BatteryDTO;

@Repository
public class BatteryDAO extends AbstractJpaDAO<BatteryDTO> {

	public BatteryDAO() {
		setClazz(BatteryDTO.class);
	}
}
