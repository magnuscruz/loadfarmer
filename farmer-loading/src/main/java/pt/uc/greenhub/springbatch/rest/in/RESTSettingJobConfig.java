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

import pt.uc.greenhub.springbatch.common.LoggingSettingProcessor;
import pt.uc.greenhub.springbatch.common.LoggingSettingWriter;
import pt.uc.greenhub.springbatch.web.controller.SettingDTO;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class RESTSettingJobConfig {

    private static final String PROPERTY_REST_API_URL = "rest.api.to.database.job.api.url.setting";

    @Bean
    ItemReader<SettingDTO> restSettingReader(Environment environment, RestTemplate restTemplate) {
        return new RESTSettingReader(environment.getRequiredProperty(PROPERTY_REST_API_URL), restTemplate);
    }

    @Bean
    ItemProcessor<SettingDTO, SettingDTO> restSettingProcessor() {
        return new LoggingSettingProcessor();
    }

    @Bean
    ItemWriter<SettingDTO> restSettingWriter() {
        return new LoggingSettingWriter();
    }

    @Bean
    Step restSettingStep(ItemReader<SettingDTO> restSettingReader,
                         ItemProcessor<SettingDTO, SettingDTO> restSettingProcessor,
                         ItemWriter<SettingDTO> restSettingWriter,
                         StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("restSettingStep")
                .<SettingDTO, SettingDTO>chunk(1)
                .reader(restSettingReader)
                .processor(restSettingProcessor)
                .writer(restSettingWriter)
                .build();
    }

    @Bean
    Job restSettingJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("restSettingStep") Step restSettingStep) {
        return jobBuilderFactory.get("restSettingJob")
                .incrementer(new RunIdIncrementer())
                .flow(restSettingStep)
                .end()
                .build();
    }
}
