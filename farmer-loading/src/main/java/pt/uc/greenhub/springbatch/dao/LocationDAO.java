package pt.uc.greenhub.springbatch.dao;

import org.springframework.stereotype.Repository;

import pt.uc.greenhub.springbatch.web.controller.LocationDTO;

@Repository
public class LocationDAO extends AbstractJpaDAO<LocationDTO> {

	public LocationDAO() {
		setClazz(LocationDTO.class);
	}
}
