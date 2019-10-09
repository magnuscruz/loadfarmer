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

import pt.uc.greenhub.springbatch.common.LoggingSampleProcessor;
import pt.uc.greenhub.springbatch.common.LoggingSampleWriter;
import pt.uc.greenhub.springbatch.web.controller.SampleDTO;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class RESTSampleJobConfig {

    private static final String PROPERTY_REST_API_URL = "rest.api.to.database.job.api.url.sample";

    @Bean
    ItemReader<SampleDTO> restSampleReader(Environment environment, RestTemplate restTemplate) {
        return new RESTSampleReader(environment.getRequiredProperty(PROPERTY_REST_API_URL), restTemplate);
    }

    @Bean
    ItemProcessor<SampleDTO, SampleDTO> restSampleProcessor() {
        return new LoggingSampleProcessor();
    }

    @Bean
    ItemWriter<SampleDTO> restSampleWriter() {
        return new LoggingSampleWriter();
    }

    @Bean
    Step restSampleStep(ItemReader<SampleDTO> restSampleReader,
                         ItemProcessor<SampleDTO, SampleDTO> restSampleProcessor,
                         ItemWriter<SampleDTO> restSampleWriter,
                         StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("restSampleStep")
                .<SampleDTO, SampleDTO>chunk(1)
                .reader(restSampleReader)
                .processor(restSampleProcessor)
                .writer(restSampleWriter)
                .build();
    }

    @Bean
    Job restSampleJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("restSampleStep") Step restSampleStep) {
        return jobBuilderFactory.get("restSampleJob")
                .incrementer(new RunIdIncrementer())
                .flow(restSampleStep)
                .end()
                .build();
    }
}
