package pt.uc.greenhub.springbatch.rest.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pt.uc.greenhub.springbatch.web.controller.SampleDTO;

import java.util.Arrays;
import java.util.List;

/**
 * This class demonstrates how we can read the input of our batch job from an
 * external REST API.
 *
 * @author Petri Kainulainen
 */
class RESTSampleReader implements ItemReader<SampleDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTSampleReader.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextStudentIndex;
    private List<SampleDTO> studentData;

    RESTSampleReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextStudentIndex = 0;
    }

    @Override
    public SampleDTO read() throws Exception {
        LOGGER.info("Reading the information of the next student");

        if (studentDataIsNotInitialized()) {
            studentData = fetchStudentDataFromAPI();
        }

        SampleDTO nextStudent = null;

        if (nextStudentIndex < studentData.size()) {
            nextStudent = studentData.get(nextStudentIndex);
            nextStudentIndex++;
        }

        LOGGER.info("Found student: {}", nextStudent);

        return nextStudent;
    }

    private boolean studentDataIsNotInitialized() {
        return this.studentData == null;
    }

    private List<SampleDTO> fetchStudentDataFromAPI() {
        LOGGER.debug("Fetching student data from an external API by using the url: {}", apiUrl);

        ResponseEntity<SampleDTO[]> response = restTemplate.getForEntity(apiUrl, SampleDTO[].class);
        SampleDTO[] studentData = response.getBody();
        LOGGER.debug("Found {} students", studentData.length);

        return Arrays.asList(studentData);
    }
}
