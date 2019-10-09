package pt.uc.greenhub.springbatch.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pt.uc.greenhub.springbatch.dao.NetworkDAO;

/**
 * @author Petri Kainulainen
 */
@RestController
@RequestMapping("/api/network")
public class NetworkController {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkController.class);

	@Autowired
	private NetworkDAO networkDAO;

	@RequestMapping(value = "/all/{pageSize}/{index}", method = RequestMethod.GET)
	public List<NetworkDTO> findAllNetworks(@PathVariable("pageSize") int pageSize, @PathVariable("index") int index) {
		LOGGER.info("Finding all students");
		return networkDAO.findAll(pageSize, index);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public NetworkDTO findByIdNetwork(@PathVariable("id") long id) {
		LOGGER.info("Finding all students");
		return networkDAO.findOne(id);
	}
}
