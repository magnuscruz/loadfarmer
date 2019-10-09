package pt.uc.greenhub.springbatch.rest.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pt.uc.greenhub.springbatch.web.controller.ProcessDTO;

import java.util.Arrays;
import java.util.List;

/**
 * This class demonstrates how we can read the input of our batch job from an
 * external REST API.
 *
 * @author Petri Kainulainen
 */
class RESTProcessReader implements ItemReader<ProcessDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTProcessReader.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextProcessIndex;
    private List<ProcessDTO> processData;

    RESTProcessReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextProcessIndex = 0;
    }

    @Override
    public ProcessDTO read() throws Exception {
        LOGGER.info("Reading the information of the next process");

        if (processDataIsNotInitialized()) {
            processData = fetchProcessDataFromAPI();
        }

        ProcessDTO nextProcess = null;

        if (nextProcessIndex < processData.size()) {
            nextProcess = processData.get(nextProcessIndex);
            nextProcessIndex++;
        }

        LOGGER.info("Found process: {}", nextProcess);

        return nextProcess;
    }

    private boolean processDataIsNotInitialized() {
        return this.processData == null;
    }

    private List<ProcessDTO> fetchProcessDataFromAPI() {
        LOGGER.debug("Fetching process data from an external API by using the url: {}", apiUrl);

        ResponseEntity<ProcessDTO[]> response = restTemplate.getForEntity(apiUrl, ProcessDTO[].class);
        ProcessDTO[] processData = response.getBody();
        LOGGER.debug("Found {} processs", processData.length);

        return Arrays.asList(processData);
    }
}
