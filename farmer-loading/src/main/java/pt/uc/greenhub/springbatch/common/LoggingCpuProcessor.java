package pt.uc.greenhub.springbatch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import pt.uc.greenhub.springbatch.web.controller.CpuDTO;

/**
 * This custom {@code ItemCpuor} simply writes the information of the
 * cpued student to the log and returns the cpu object.
 *
 * @author Petri Kainulainen
 */
public class LoggingCpuProcessor implements ItemProcessor<CpuDTO, CpuDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCpuProcessor.class);

    @Override
    public CpuDTO process(CpuDTO item) throws Exception {
        LOGGER.info("Cpuing cpu information: {}", item);
        return item;
    }
}
