package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import pt.uc.greenhub.springbatch.web.controller.BatteryDTO;

/**
 * This custom {@code ItemBatteryor} simply writes the information of the
 * batteryed student to the log and returns the batteryed object.
 *
 * @author Petri Kainulainen
 */
public class LoggingBatteryProcessor implements ItemProcessor<BatteryDTO, BatteryDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingBatteryProcessor.class);

    @Override
    public BatteryDTO process(BatteryDTO item) throws Exception {
        LOGGER.info("Batterying battery information: {}", item);
        return item;
    }
}
