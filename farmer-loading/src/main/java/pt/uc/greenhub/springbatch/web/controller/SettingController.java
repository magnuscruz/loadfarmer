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

import pt.uc.greenhub.springbatch.dao.SettingDAO;

/**
 * @author Petri Kainulainen
 */
@RestController
@RequestMapping("/api/setting")
public class SettingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SettingController.class);

	@Autowired
	private SettingDAO settingDAO;

	@RequestMapping(value = "/all/{pageSize}/{index}", method = RequestMethod.GET)
	public List<SettingDTO> findAllSettings(@PathVariable("pageSize") int pageSize, @PathVariable("index") int index) {
		LOGGER.info("Finding all students");
		return settingDAO.findAll(pageSize, index);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public SettingDTO findByIdSetting(@PathVariable("id") long id) {
		LOGGER.info("Finding all students");
		return settingDAO.findOne(id);
	}
}
