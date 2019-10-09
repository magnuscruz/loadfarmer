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

import pt.uc.greenhub.springbatch.dao.CpuDAO;

/**
 * @author Petri Kainulainen
 */
@RestController
@RequestMapping("/api/cpu")
public class CpuController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CpuController.class);

	@Autowired
	private CpuDAO cpuDAO;

	@RequestMapping(value = "/all/{pageSize}/{index}", method = RequestMethod.GET)
	public List<CpuDTO> findAllCpus(@PathVariable("pageSize") int pageSize, @PathVariable("index") int index) {
		LOGGER.info("Finding all students");
		return cpuDAO.findAll(pageSize, index);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public CpuDTO findByIdCpu(@PathVariable("id") long id) {
		LOGGER.info("Finding all students");
		return cpuDAO.findOne(id);
	}
}
