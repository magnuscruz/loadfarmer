package pt.uc.greenhub.springbatch.rest.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pt.uc.greenhub.springbatch.web.controller.LocationDTO;

import java.util.Arrays;
import java.util.List;

/**
 * This class demonstrates how we can read the input of our batch job from an
 * external REST API.
 *
 * @author Petri Kainulainen
 */
class RESTLocationReader implements ItemReader<LocationDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTLocationReader.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextLocationIndex;
    private List<LocationDTO> locationData;

    RESTLocationReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextLocationIndex = 0;
    }

    @Override
    public LocationDTO read() throws Exception {
        LOGGER.info("Reading the information of the next location");

        if (locationDataIsNotInitialized()) {
            locationData = fetchLocationDataFromAPI();
        }

        LocationDTO nextLocation = null;

        if (nextLocationIndex < locationData.size()) {
            nextLocation = locationData.get(nextLocationIndex);
            nextLocationIndex++;
        }

        LOGGER.info("Found location: {}", nextLocation);

        return nextLocation;
    }

    private boolean locationDataIsNotInitialized() {
        return this.locationData == null;
    }

    private List<LocationDTO> fetchLocationDataFromAPI() {
        LOGGER.debug("Fetching location data from an external API by using the url: {}", apiUrl);

        ResponseEntity<LocationDTO[]> response = restTemplate.getForEntity(apiUrl, LocationDTO[].class);
        LocationDTO[] locationData = response.getBody();
        LOGGER.debug("Found {} locations", locationData.length);

        return Arrays.asList(locationData);
    }
}
