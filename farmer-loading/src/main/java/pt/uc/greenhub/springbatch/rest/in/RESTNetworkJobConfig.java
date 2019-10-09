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

import pt.uc.greenhub.springbatch.common.LoggingNetworkProcessor;
import pt.uc.greenhub.springbatch.common.LoggingNetworkWriter;
import pt.uc.greenhub.springbatch.web.controller.NetworkDTO;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class RESTNetworkJobConfig {

    private static final String PROPERTY_REST_API_URL = "rest.api.to.database.job.api.url.network";

    @Bean
    ItemReader<NetworkDTO> restNetworkReader(Environment environment, RestTemplate restTemplate) {
        return new RESTNetworkReader(environment.getRequiredProperty(PROPERTY_REST_API_URL), restTemplate);
    }

    @Bean
    ItemProcessor<NetworkDTO, NetworkDTO> restNetworkProcessor() {
        return new LoggingNetworkProcessor();
    }

    @Bean
    ItemWriter<NetworkDTO> restNetworkWriter() {
        return new LoggingNetworkWriter();
    }

    @Bean
    Step restNetworkStep(ItemReader<NetworkDTO> restNetworkReader,
                         ItemProcessor<NetworkDTO, NetworkDTO> restNetworkProcessor,
                         ItemWriter<NetworkDTO> restNetworkWriter,
                         StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("restNetworkStep")
                .<NetworkDTO, NetworkDTO>chunk(1)
                .reader(restNetworkReader)
                .processor(restNetworkProcessor)
                .writer(restNetworkWriter)
                .build();
    }

    @Bean
    Job restNetworkJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("restNetworkStep") Step restNetworkStep) {
        return jobBuilderFactory.get("restNetworkJob")
                .incrementer(new RunIdIncrementer())
                .flow(restNetworkStep)
                .end()
                .build();
    }
}
