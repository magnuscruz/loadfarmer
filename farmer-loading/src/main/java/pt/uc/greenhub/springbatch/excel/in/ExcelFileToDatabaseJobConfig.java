package pt.uc.greenhub.springbatch.excel.in;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.mapping.BeanWrapperRowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import pt.uc.greenhub.springbatch.common.LoggingSampleProcessor;
import pt.uc.greenhub.springbatch.common.LoggingSampleWriter;
import pt.uc.greenhub.springbatch.web.controller.SampleDTO;

/**
 * @author Petri Kainulainen
 */
//@Configuration
public class ExcelFileToDatabaseJobConfig {

    private static final String PROPERTY_EXCEL_SOURCE_FILE_PATH = "excel.to.database.job.source.file.path";

    @Bean
    ItemReader<SampleDTO> excelSampleReader(Environment environment) {
        PoiItemReader<SampleDTO> reader = new PoiItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource(environment.getRequiredProperty(PROPERTY_EXCEL_SOURCE_FILE_PATH)));
        reader.setRowMapper(excelRowMapper());
        return reader;
    }

    private RowMapper<SampleDTO> excelRowMapper() {
        BeanWrapperRowMapper<SampleDTO> rowMapper = new BeanWrapperRowMapper<>();
        rowMapper.setTargetType(SampleDTO.class);
        return rowMapper;
    }

    /**
     * If your Excel document has no header, you have to create a custom
     * row mapper and configure it here.
     */
    /*private RowMapper<SampleDTO> excelRowMapper() {
       return new SampleExcelRowMapper();
    }*/

    @Bean
    ItemProcessor<SampleDTO, SampleDTO> excelSampleProcessor() {
        return new LoggingSampleProcessor();
    }

    @Bean
    ItemWriter<SampleDTO> excelSampleWriter() {
        return new LoggingSampleWriter();
    }

    @Bean
    Step excelFileToDatabaseStep(ItemReader<SampleDTO> excelSampleReader,
                                 ItemProcessor<SampleDTO, SampleDTO> excelSampleProcessor,
                                 ItemWriter<SampleDTO> excelSampleWriter,
                                 StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("excelFileToDatabaseStep")
                .<SampleDTO, SampleDTO>chunk(1)
                .reader(excelSampleReader)
                .processor(excelSampleProcessor)
                .writer(excelSampleWriter)
                .build();
    }

    @Bean
    Job excelFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
                               @Qualifier("excelFileToDatabaseStep") Step excelSampleStep) {
        return jobBuilderFactory.get("excelFileToDatabaseJob")
                .incrementer(new RunIdIncrementer())
                .flow(excelSampleStep)
                .end()
                .build();
    }
}
