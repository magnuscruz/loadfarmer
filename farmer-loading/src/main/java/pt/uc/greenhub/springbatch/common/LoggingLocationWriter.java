package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import pt.uc.greenhub.springbatch.web.controller.LocationDTO;

import java.util.List;

/**
 * This custom {@code ItemWriter} writes the information of the location to
 * the log.
 *
 * @author Petri Kainulainen
 */
public class LoggingLocationWriter implements ItemWriter<LocationDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingLocationWriter.class);

    @Override
    public void write(List<? extends LocationDTO> items) throws Exception {
        LOGGER.info("Received the information of {} batteries", items.size());

        items.forEach(i -> LOGGER.debug("Received the information of a batteries: {}", i));
    }
}
