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

import pt.uc.greenhub.springbatch.dao.BatteryDAO;

/**
 * @author Petri Kainulainen
 */
@RestController
@RequestMapping("/api/battery")
public class BatteryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatteryController.class);

	@Autowired
	private BatteryDAO batteryDAO;

	@RequestMapping(value = "/all/{pageSize}/{index}", method = RequestMethod.GET)
	public List<BatteryDTO> findAllBatterys(@PathVariable("pageSize") int pageSize, @PathVariable("index") int index) {
		LOGGER.info("Finding all students");
		return batteryDAO.findAll(pageSize, index);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public BatteryDTO findByIdBattery(@PathVariable("id") long id) {
		LOGGER.info("Finding all students");
		return batteryDAO.findOne(id);
	}
}
