package pt.uc.greenhub.springbatch.rest.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pt.uc.greenhub.springbatch.web.controller.CpuDTO;

import java.util.Arrays;
import java.util.List;

/**
 * This class demonstrates how we can read the input of our batch job from an
 * external REST API.
 *
 * @author Petri Kainulainen
 */
class RESTCpuReader implements ItemReader<CpuDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTCpuReader.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextCpuIndex;
    private List<CpuDTO> cpuData;

    RESTCpuReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextCpuIndex = 0;
    }

    @Override
    public CpuDTO read() throws Exception {
        LOGGER.info("Reading the information of the next cpu");

        if (cpuDataIsNotInitialized()) {
            cpuData = fetchCpuDataFromAPI();
        }

        CpuDTO nextCpu = null;

        if (nextCpuIndex < cpuData.size()) {
            nextCpu = cpuData.get(nextCpuIndex);
            nextCpuIndex++;
        }

        LOGGER.info("Found cpu: {}", nextCpu);

        return nextCpu;
    }

    private boolean cpuDataIsNotInitialized() {
        return this.cpuData == null;
    }

    private List<CpuDTO> fetchCpuDataFromAPI() {
        LOGGER.debug("Fetching cpu data from an external API by using the url: {}", apiUrl);

        ResponseEntity<CpuDTO[]> response = restTemplate.getForEntity(apiUrl, CpuDTO[].class);
        CpuDTO[] cpuData = response.getBody();
        LOGGER.debug("Found {} cpus", cpuData.length);

        return Arrays.asList(cpuData);
    }
}
