package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import pt.uc.greenhub.springbatch.web.controller.BatteryDTO;

import java.util.List;

/**
 * This custom {@code ItemWriter} writes the information of the battery to
 * the log.
 *
 * @author Petri Kainulainen
 */
public class LoggingBatteryWriter implements ItemWriter<BatteryDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingBatteryWriter.class);

    @Override
    public void write(List<? extends BatteryDTO> items) throws Exception {
        LOGGER.info("Received the information of {} batteries", items.size());

        items.forEach(i -> LOGGER.debug("Received the information of a batteries: {}", i));
    }
}
