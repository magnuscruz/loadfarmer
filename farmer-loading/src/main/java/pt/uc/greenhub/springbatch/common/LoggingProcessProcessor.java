package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import pt.uc.greenhub.springbatch.web.controller.ProcessDTO;

/**
 * This custom {@code ItemProcessor} simply writes the information of the
 * processed student to the log and returns the processed object.
 *
 * @author Petri Kainulainen
 */
public class LoggingProcessProcessor implements ItemProcessor<ProcessDTO, ProcessDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingProcessProcessor.class);

    @Override
    public ProcessDTO process(ProcessDTO item) throws Exception {
        LOGGER.info("Processing process information: {}", item);
        return item;
    }
}
