package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import pt.uc.greenhub.springbatch.web.controller.NetworkDTO;

import java.util.List;

/**
 * This custom {@code ItemWriter} writes the information of the network to
 * the log.
 *
 * @author Petri Kainulainen
 */
public class LoggingNetworkWriter implements ItemWriter<NetworkDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingNetworkWriter.class);

    @Override
    public void write(List<? extends NetworkDTO> items) throws Exception {
        LOGGER.info("Received the information of {} batteries", items.size());

        items.forEach(i -> LOGGER.debug("Received the information of a batteries: {}", i));
    }
}
