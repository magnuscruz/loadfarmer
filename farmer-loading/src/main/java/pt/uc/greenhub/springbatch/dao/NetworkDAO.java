package pt.uc.greenhub.springbatch.dao;

import org.springframework.stereotype.Repository;

import pt.uc.greenhub.springbatch.web.controller.NetworkDTO;

@Repository
public class NetworkDAO extends AbstractJpaDAO<NetworkDTO> {

	public NetworkDAO() {
		setClazz(NetworkDTO.class);
	}
}
