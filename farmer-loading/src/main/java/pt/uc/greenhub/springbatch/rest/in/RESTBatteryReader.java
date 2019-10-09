package pt.uc.greenhub.springbatch.rest.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pt.uc.greenhub.springbatch.web.controller.BatteryDTO;

import java.util.Arrays;
import java.util.List;

/**
 * This class demonstrates how we can read the input of our batch job from an
 * external REST API.
 *
 * @author Petri Kainulainen
 */
class RESTBatteryReader implements ItemReader<BatteryDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTBatteryReader.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextBatteryIndex;
    private List<BatteryDTO> batteryData;

    RESTBatteryReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextBatteryIndex = 0;
    }

    @Override
    public BatteryDTO read() throws Exception {
        LOGGER.info("Reading the information of the next battery");

        if (batteryDataIsNotInitialized()) {
            batteryData = fetchBatteryDataFromAPI();
        }

        BatteryDTO nextBattery = null;

        if (nextBatteryIndex < batteryData.size()) {
            nextBattery = batteryData.get(nextBatteryIndex);
            nextBatteryIndex++;
        }

        LOGGER.info("Found battery: {}", nextBattery);

        return nextBattery;
    }

    private boolean batteryDataIsNotInitialized() {
        return this.batteryData == null;
    }

    private List<BatteryDTO> fetchBatteryDataFromAPI() {
        LOGGER.debug("Fetching battery data from an external API by using the url: {}", apiUrl);

        ResponseEntity<BatteryDTO[]> response = restTemplate.getForEntity(apiUrl, BatteryDTO[].class);
        BatteryDTO[] batteryData = response.getBody();
        LOGGER.debug("Found {} batterys", batteryData.length);

        return Arrays.asList(batteryData);
    }
}
