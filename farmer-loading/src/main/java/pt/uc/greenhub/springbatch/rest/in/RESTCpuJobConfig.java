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

import pt.uc.greenhub.springbatch.common.LoggingCpuProcessor;
import pt.uc.greenhub.springbatch.common.LoggingCpuWriter;
import pt.uc.greenhub.springbatch.web.controller.CpuDTO;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class RESTCpuJobConfig {

    private static final String PROPERTY_REST_API_URL = "rest.api.to.database.job.api.url.cpu";

    @Bean
    ItemReader<CpuDTO> restCpuReader(Environment environment, RestTemplate restTemplate) {
        return new RESTCpuReader(environment.getRequiredProperty(PROPERTY_REST_API_URL), restTemplate);
    }

    @Bean
    ItemProcessor<CpuDTO, CpuDTO> restCpuProcessor() {
        return new LoggingCpuProcessor();
    }

    @Bean
    ItemWriter<CpuDTO> restCpuWriter() {
        return new LoggingCpuWriter();
    }

    @Bean
    Step restCpuStep(ItemReader<CpuDTO> restCpuReader,
                         ItemProcessor<CpuDTO, CpuDTO> restCpuProcessor,
                         ItemWriter<CpuDTO> restCpuWriter,
                         StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("restCpuStep")
                .<CpuDTO, CpuDTO>chunk(1)
                .reader(restCpuReader)
                .processor(restCpuProcessor)
                .writer(restCpuWriter)
                .build();
    }

    @Bean
    Job restCpuJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("restCpuStep") Step restCpuStep) {
        return jobBuilderFactory.get("restCpuJob")
                .incrementer(new RunIdIncrementer())
                .flow(restCpuStep)
                .end()
                .build();
    }
}
