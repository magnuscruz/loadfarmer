package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import pt.uc.greenhub.springbatch.web.controller.NetworkDTO;

/**
 * This custom {@code ItemNetworkor} simply writes the information of the
 * networked student to the log and returns the networked object.
 *
 * @author Petri Kainulainen
 */
public class LoggingNetworkProcessor implements ItemProcessor<NetworkDTO, NetworkDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingNetworkProcessor.class);

    @Override
    public NetworkDTO process(NetworkDTO item) throws Exception {
        LOGGER.info("Networking network information: {}", item);
        return item;
    }
}
