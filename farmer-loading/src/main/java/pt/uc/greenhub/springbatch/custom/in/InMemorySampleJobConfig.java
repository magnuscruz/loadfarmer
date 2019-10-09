package pt.uc.greenhub.springbatch.custom.in;

import pt.uc.greenhub.springbatch.common.LoggingSampleProcessor;
import pt.uc.greenhub.springbatch.common.LoggingSampleWriter;
import pt.uc.greenhub.springbatch.web.controller.SampleDTO;

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

/**
 * @author Petri Kainulainen
 */
//@Configuration
public class InMemorySampleJobConfig {

    @Bean
    ItemReader<SampleDTO> inMemorySampleReader() {
        return new InMemorySampleReader();
    }

    @Bean
    ItemProcessor<SampleDTO, SampleDTO> inMemorySampleProcessor() {
        return new LoggingSampleProcessor();
    }

    @Bean
    ItemWriter<SampleDTO> inMemorySampleWriter() {
        return new LoggingSampleWriter();
    }

    @Bean
    Step inMemorySampleStep(ItemReader<SampleDTO> inMemorySampleReader,
                             ItemProcessor<SampleDTO, SampleDTO> inMemorySampleProcessor,
                             ItemWriter<SampleDTO> inMemorySampleWriter,
                             StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("inMemorySampleStep")
                .<SampleDTO, SampleDTO>chunk(1)
                .reader(inMemorySampleReader)
                .processor(inMemorySampleProcessor)
                .writer(inMemorySampleWriter)
                .build();
    }

    @Bean
    Job inMemorySampleJob(JobBuilderFactory jobBuilderFactory,
                           @Qualifier("inMemorySampleStep") Step inMemorySampleStep) {
        return jobBuilderFactory.get("inMemorySampleJob")
                .incrementer(new RunIdIncrementer())
                .flow(inMemorySampleStep)
                .end()
                .build();
    }
}
