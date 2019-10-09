package pt.uc.greenhub.springbatch.xml.in;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import pt.uc.greenhub.springbatch.common.LoggingSampleProcessor;
import pt.uc.greenhub.springbatch.web.controller.SampleDTO;

/**
 * @author Petri Kainulainen
 */
//@Configuration
public class XmlFileToDatabaseJobConfig {

    private static final String PROPERTY_XML_SOURCE_FILE_PATH = "xml.to.database.job.source.file.path";
    private static final String QUERY_INSERT_STUDENT = "INSERT " +
            "INTO samples(id, device_id, timestamp, app_version, database_version, battery_stage, battery_level) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    @Bean
    ItemReader<SampleDTO> xmlFileItemReader(Environment environment) {
        StaxEventItemReader<SampleDTO> xmlFileReader = new StaxEventItemReader<>();
        xmlFileReader.setResource(new ClassPathResource(environment.getRequiredProperty(PROPERTY_XML_SOURCE_FILE_PATH)));
        xmlFileReader.setFragmentRootElementName("student");

        Jaxb2Marshaller studentMarshaller = new Jaxb2Marshaller();
        studentMarshaller.setClassesToBeBound(SampleDTO.class);

        xmlFileReader.setUnmarshaller(studentMarshaller);
        return xmlFileReader;
    }

    @Bean
    ItemProcessor<SampleDTO, SampleDTO> xmlFileItemProcessor() {
        return new LoggingSampleProcessor();
    }

    @Bean
    ItemWriter<SampleDTO> xmlFileDatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        JdbcBatchItemWriter<SampleDTO> databaseItemWriter = new JdbcBatchItemWriter<>();
        databaseItemWriter.setDataSource(dataSource);
        databaseItemWriter.setJdbcTemplate(jdbcTemplate);

        databaseItemWriter.setSql(QUERY_INSERT_STUDENT);

        ItemPreparedStatementSetter<SampleDTO> studentPreparedStatementSetter = new SamplePreparedStatementSetter();
        databaseItemWriter.setItemPreparedStatementSetter(studentPreparedStatementSetter);

        return databaseItemWriter;
    }
    @Bean
    Step xmlFileToDatabaseStep(ItemReader<SampleDTO> xmlFileItemReader,
                               ItemProcessor<SampleDTO, SampleDTO> xmlFileItemProcessor,
                               ItemWriter<SampleDTO> xmlFileDatabaseItemWriter,
                               StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("xmlFileToDatabaseStep")
                .<SampleDTO, SampleDTO>chunk(1)
                .reader(xmlFileItemReader)
                .processor(xmlFileItemProcessor)
                .writer(xmlFileDatabaseItemWriter)
                .build();
    } 

    @Bean
    Job xmlFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
                             @Qualifier("xmlFileToDatabaseStep") Step xmlSampleStep) {
        return jobBuilderFactory.get("xmlFileToDatabaseJob")
                .incrementer(new RunIdIncrementer())
                .flow(xmlSampleStep)
                .end()
                .build();
    }
}
