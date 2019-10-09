package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import pt.uc.greenhub.springbatch.web.controller.SettingDTO;

/**
 * This custom {@code ItemSettingor} simply writes the information of the
 * settinged student to the log and returns the settinged object.
 *
 * @author Petri Kainulainen
 */
public class LoggingSettingProcessor implements ItemProcessor<SettingDTO, SettingDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingSettingProcessor.class);

    @Override
    public SettingDTO process(SettingDTO item) throws Exception {
        LOGGER.info("Settinging setting information: {}", item);
        return item;
    }
}
