package pt.uc.greenhub.springbatch.rest.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pt.uc.greenhub.springbatch.web.controller.NetworkDTO;

import java.util.Arrays;
import java.util.List;

/**
 * This class demonstrates how we can read the input of our batch job from an
 * external REST API.
 *
 * @author Petri Kainulainen
 */
class RESTNetworkReader implements ItemReader<NetworkDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTNetworkReader.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextNetworkIndex;
    private List<NetworkDTO> networkData;

    RESTNetworkReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextNetworkIndex = 0;
    }

    @Override
    public NetworkDTO read() throws Exception {
        LOGGER.info("Reading the information of the next network");

        if (networkDataIsNotInitialized()) {
            networkData = fetchNetworkDataFromAPI();
        }

        NetworkDTO nextNetwork = null;

        if (nextNetworkIndex < networkData.size()) {
            nextNetwork = networkData.get(nextNetworkIndex);
            nextNetworkIndex++;
        }

        LOGGER.info("Found network: {}", nextNetwork);

        return nextNetwork;
    }

    private boolean networkDataIsNotInitialized() {
        return this.networkData == null;
    }

    private List<NetworkDTO> fetchNetworkDataFromAPI() {
        LOGGER.debug("Fetching network data from an external API by using the url: {}", apiUrl);

        ResponseEntity<NetworkDTO[]> response = restTemplate.getForEntity(apiUrl, NetworkDTO[].class);
        NetworkDTO[] networkData = response.getBody();
        LOGGER.debug("Found {} networks", networkData.length);

        return Arrays.asList(networkData);
    }
}
