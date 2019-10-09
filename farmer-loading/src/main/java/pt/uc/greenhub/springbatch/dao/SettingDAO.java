package pt.uc.greenhub.springbatch.dao;

import org.springframework.stereotype.Repository;

import pt.uc.greenhub.springbatch.web.controller.SettingDTO;

@Repository
public class SettingDAO extends AbstractJpaDAO<SettingDTO> {

	public SettingDAO() {
		setClazz(SettingDTO.class);
	}
}
