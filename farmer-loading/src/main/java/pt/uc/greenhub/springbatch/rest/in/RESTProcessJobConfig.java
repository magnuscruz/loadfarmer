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

import pt.uc.greenhub.springbatch.common.LoggingProcessProcessor;
import pt.uc.greenhub.springbatch.common.LoggingProcessWriter;
import pt.uc.greenhub.springbatch.web.controller.ProcessDTO;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class RESTProcessJobConfig {

    private static final String PROPERTY_REST_API_URL = "rest.api.to.database.job.api.url.process";

    @Bean
    ItemReader<ProcessDTO> restProcessReader(Environment environment, RestTemplate restTemplate) {
        return new RESTProcessReader(environment.getRequiredProperty(PROPERTY_REST_API_URL), restTemplate);
    }

    @Bean
    ItemProcessor<ProcessDTO, ProcessDTO> restProcessProcessor() {
        return new LoggingProcessProcessor();
    }

    @Bean
    ItemWriter<ProcessDTO> restProcessWriter() {
        return new LoggingProcessWriter();
    }

    @Bean
    Step restProcessStep(ItemReader<ProcessDTO> restProcessReader,
                         ItemProcessor<ProcessDTO, ProcessDTO> restProcessProcessor,
                         ItemWriter<ProcessDTO> restProcessWriter,
                         StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("restProcessStep")
                .<ProcessDTO, ProcessDTO>chunk(1)
                .reader(restProcessReader)
                .processor(restProcessProcessor)
                .writer(restProcessWriter)
                .build();
    }

    @Bean
    Job restProcessJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("restProcessStep") Step restProcessStep) {
        return jobBuilderFactory.get("restProcessJob")
                .incrementer(new RunIdIncrementer())
                .flow(restProcessStep)
                .end()
                .build();
    }
}
