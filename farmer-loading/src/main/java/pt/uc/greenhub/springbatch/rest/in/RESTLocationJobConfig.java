package pt.uc.greenhub.springbatch.rest.in;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import pt.uc.greenhub.springbatch.common.LoggingLocationProcessor;
import pt.uc.greenhub.springbatch.common.LoggingLocationWriter;
import pt.uc.greenhub.springbatch.web.controller.LocationDTO;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class RESTLocationJobConfig {

    private static final String PROPERTY_REST_API_URL = "rest.api.to.database.job.api.url.location";

    @Bean
    ItemReader<LocationDTO> restLocationReader(Environment environment, RestTemplate restTemplate) {
        return new RESTLocationReader(environment.getRequiredProperty(PROPERTY_REST_API_URL), restTemplate);
    }

    @Bean
    ItemProcessor<LocationDTO, LocationDTO> restLocationProcessor() {
        return new LoggingLocationProcessor();
    }

    @Bean
    ItemWriter<LocationDTO> restLocationWriter() {
        return new LoggingLocationWriter();
    }

    @Bean
    Step restLocationStep(ItemReader<LocationDTO> restLocationReader,
                         ItemProcessor<LocationDTO, LocationDTO> restLocationProcessor,
                         ItemWriter<LocationDTO> restLocationWriter,
                         StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("restLocationStep")
                .<LocationDTO, LocationDTO>chunk(1)
                .reader(restLocationReader)
                .processor(restLocationProcessor)
                .writer(restLocationWriter)
                .build();
    }

    @Bean
    Job restLocationJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("restLocationStep") Step restLocationStep) {
        return jobBuilderFactory.get("restLocationJob")
                .incrementer(new RunIdIncrementer())
                .flow(restLocationStep)
                .end()
                .build();
    }
}
