package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import pt.uc.greenhub.springbatch.web.controller.ProcessDTO;

import java.util.List;

/**
 * This custom {@code ItemWriter} writes the information of the student to
 * the log.
 *
 * @author Petri Kainulainen
 */
public class LoggingProcessWriter implements ItemWriter<ProcessDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingProcessWriter.class);

    @Override
    public void write(List<? extends ProcessDTO> items) throws Exception {
        LOGGER.info("Received the information of {} processs", items.size());

        items.forEach(i -> LOGGER.debug("Received the information of a processs: {}", i));
    }
}
