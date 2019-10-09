package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import pt.uc.greenhub.springbatch.web.controller.CpuDTO;

import java.util.List;

/**
 * This custom {@code ItemWriter} writes the information of the cpu to
 * the log.
 *
 * @author Petri Kainulainen
 */
public class LoggingCpuWriter implements ItemWriter<CpuDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCpuWriter.class);

    @Override
    public void write(List<? extends CpuDTO> items) throws Exception {
        LOGGER.info("Received the information of {} batteries", items.size());

        items.forEach(i -> LOGGER.debug("Received the information of a batteries: {}", i));
    }
}
