package net.petrikainulainen.springbatch.common;

import net.petrikainulainen.springbatch.sample.SampleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * This custom {@code ItemWriter} writes the information of the student to
 * the log.
 *
 * @author Petri Kainulainen
 */
public class LoggingSampleWriter implements ItemWriter<SampleDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingSampleWriter.class);

    @Override
    public void write(List<? extends SampleDTO> items) throws Exception {
        LOGGER.info("Received the information of {} samples", items.size());

        items.forEach(i -> LOGGER.debug("Received the information of a samples: {}", i));
    }
}
