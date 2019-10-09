package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import pt.uc.greenhub.springbatch.web.controller.SampleDTO;

/**
 * This custom {@code ItemProcessor} simply writes the information of the
 * processed student to the log and returns the processed object.
 *
 * @author Petri Kainulainen
 */
public class LoggingSampleProcessor implements ItemProcessor<SampleDTO, SampleDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingSampleProcessor.class);

    @Override
    public SampleDTO process(SampleDTO item) throws Exception {
        LOGGER.info("Processing student information: {}", item);
        return item;
    }
}
