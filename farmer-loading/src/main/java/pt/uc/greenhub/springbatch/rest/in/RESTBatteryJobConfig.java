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

import pt.uc.greenhub.springbatch.common.LoggingBatteryProcessor;
import pt.uc.greenhub.springbatch.common.LoggingBatteryWriter;
import pt.uc.greenhub.springbatch.web.controller.BatteryDTO;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class RESTBatteryJobConfig {

    private static final String PROPERTY_REST_API_URL = "rest.api.to.database.job.api.url.battery";

    @Bean
    ItemReader<BatteryDTO> restBatteryReader(Environment environment, RestTemplate restTemplate) {
        return new RESTBatteryReader(environment.getRequiredProperty(PROPERTY_REST_API_URL), restTemplate);
    }

    @Bean
    ItemProcessor<BatteryDTO, BatteryDTO> restBatteryProcessor() {
        return new LoggingBatteryProcessor();
    }

    @Bean
    ItemWriter<BatteryDTO> restBatteryWriter() {
        return new LoggingBatteryWriter();
    }

    @Bean
    Step restBatteryStep(ItemReader<BatteryDTO> restBatteryReader,
                         ItemProcessor<BatteryDTO, BatteryDTO> restBatteryProcessor,
                         ItemWriter<BatteryDTO> restBatteryWriter,
                         StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("restBatteryStep")
                .<BatteryDTO, BatteryDTO>chunk(1)
                .reader(restBatteryReader)
                .processor(restBatteryProcessor)
                .writer(restBatteryWriter)
                .build();
    }

    @Bean
    Job restBatteryJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("restBatteryStep") Step restBatteryStep) {
        return jobBuilderFactory.get("restBatteryJob")
                .incrementer(new RunIdIncrementer())
                .flow(restBatteryStep)
                .end()
                .build();
    }
}
