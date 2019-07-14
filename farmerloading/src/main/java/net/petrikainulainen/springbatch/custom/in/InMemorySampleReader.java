package net.petrikainulainen.springbatch.custom.in;

import net.petrikainulainen.springbatch.sample.SampleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class demonstrates how we can create a custom item reader.
 *
 * @author Petri Kainulainen
 */
public class InMemorySampleReader implements ItemReader<SampleDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySampleReader.class);

    private int nextSampleIndex;
    private List<SampleDTO> sampleData;

    InMemorySampleReader() {
        initialize();
    }

    private void initialize() {
        SampleDTO tony = new SampleDTO();
        tony.setId(1l);
        tony.setDeviceId(1);
        tony.setBatteryStage("carregando");

        SampleDTO nick = new SampleDTO();
        nick.setId(2l);
        nick.setDeviceId(2);
        nick.setBatteryStage("carregando");

        SampleDTO ian = new SampleDTO();
        ian.setId(3l);
        ian.setDeviceId(3);
        ian.setBatteryStage("descarregando");

        sampleData = Collections.unmodifiableList(Arrays.asList(tony, nick, ian));
        nextSampleIndex = 0;
    }

    @Override
    public SampleDTO read() throws Exception {
        LOGGER.info("Reading the information of the next sample");

        SampleDTO nextSample = null;

        if (nextSampleIndex < sampleData.size()) {
            nextSample = sampleData.get(nextSampleIndex);
            nextSampleIndex++;
        }

        LOGGER.info("Found sample: {}", nextSample);

        return nextSample;
    }
}
