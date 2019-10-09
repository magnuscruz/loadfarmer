package pt.uc.greenhub.springbatch.rest.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pt.uc.greenhub.springbatch.web.controller.SettingDTO;

import java.util.Arrays;
import java.util.List;

/**
 * This class demonstrates how we can read the input of our batch job from an
 * external REST API.
 *
 * @author Petri Kainulainen
 */
class RESTSettingReader implements ItemReader<SettingDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTSettingReader.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextSettingIndex;
    private List<SettingDTO> settingData;

    RESTSettingReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextSettingIndex = 0;
    }

    @Override
    public SettingDTO read() throws Exception {
        LOGGER.info("Reading the information of the next setting");

        if (settingDataIsNotInitialized()) {
            settingData = fetchSettingDataFromAPI();
        }

        SettingDTO nextSetting = null;

        if (nextSettingIndex < settingData.size()) {
            nextSetting = settingData.get(nextSettingIndex);
            nextSettingIndex++;
        }

        LOGGER.info("Found setting: {}", nextSetting);

        return nextSetting;
    }

    private boolean settingDataIsNotInitialized() {
        return this.settingData == null;
    }

    private List<SettingDTO> fetchSettingDataFromAPI() {
        LOGGER.debug("Fetching setting data from an external API by using the url: {}", apiUrl);

        ResponseEntity<SettingDTO[]> response = restTemplate.getForEntity(apiUrl, SettingDTO[].class);
        SettingDTO[] settingData = response.getBody();
        LOGGER.debug("Found {} settings", settingData.length);

        return Arrays.asList(settingData);
    }
}
