package net.petrikainulainen.springbatch.sample;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.petrikainulainen.springbatch.dao.SampleDAO;
import net.petrikainulainen.springbatch.student.StudentDTO;

/**
 * @author Petri Kainulainen
 */
@RestController
@RequestMapping("/api/sample")
public class SampleController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SampleController.class);

	@Autowired
	private SampleDAO sampleDAO;

	@RequestMapping(value = "/all/{pageSize}/{index}", method = RequestMethod.GET)
	public List<SampleDTO> findAllSamples(@PathVariable("pageSize") int pageSize, @PathVariable("index") int index) {
		LOGGER.info("Finding all students");
		return sampleDAO.findAll(pageSize, index);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public SampleDTO findByIdSample(@PathVariable("id") long id) {
		LOGGER.info("Finding all students");
		return sampleDAO.findOne(id);
	}
}
