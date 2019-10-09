package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import pt.uc.greenhub.springbatch.web.controller.LocationDTO;

/**
 * This custom {@code ItemLocationor} simply writes the information of the
 * locationed student to the log and returns the location object.
 *
 * @author Petri Kainulainen
 */
public class LoggingLocationProcessor implements ItemProcessor<LocationDTO, LocationDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingLocationProcessor.class);

    @Override
    public LocationDTO process(LocationDTO item) throws Exception {
        LOGGER.info("Locationing location information: {}", item);
        return item;
    }
}
