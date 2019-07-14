package net.petrikainulainen.springbatch.csv.in;

import java.beans.PropertyEditor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import net.petrikainulainen.springbatch.common.LoggingSampleProcessor;
import net.petrikainulainen.springbatch.sample.SampleDTO;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class CsvFileToDatabaseJobConfig {

    private static final String PROPERTY_CSV_SOURCE_FILE_PATH = "csv.to.database.job.source.file.path";
    private static final String QUERY_INSERT_STUDENT = "INSERT " +
    
            "INTO samples(id, device_id, timestamp, app_version, database_version, battery_stage, country_code, created, battery_level, memory_active, memory_free, memory_inactive, memory_user, memory_wired, network_status, screen_brightness, screen_on, timezone, triggered, updated) " +
            "VALUES (:id, :deviceId, :timestamp, :appVersion, :databaseVersion, :batteryStage, :countryCode, :created, :batteryLevel, :memoryActive, :memoryFree, :memoryInactive, :memoryUser, :memoryWired, :networkStatus, :screenBrightness, :screenOn, :timezone, :triggered, :updated)";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Bean
    ItemReader<SampleDTO> csvFileItemReader(Environment environment) {
        FlatFileItemReader<SampleDTO> csvFileReader = new FlatFileItemReader<>();
        try {
			csvFileReader.setResource(new FileSystemResource(environment.getRequiredProperty(PROPERTY_CSV_SOURCE_FILE_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("erro", e);
		}
        csvFileReader.setLinesToSkip(1);

        LineMapper<SampleDTO> studentLineMapper = createStudentLineMapper();
        csvFileReader.setLineMapper(studentLineMapper);

        return csvFileReader;
    }

    private LineMapper<SampleDTO> createStudentLineMapper() {
        DefaultLineMapper<SampleDTO> studentLineMapper = new DefaultLineMapper<>();

        LineTokenizer studentLineTokenizer = createStudentLineTokenizer();
        studentLineMapper.setLineTokenizer(studentLineTokenizer);

        FieldSetMapper<SampleDTO> studentInformationMapper = createStudentInformationMapper();
        studentLineMapper.setFieldSetMapper(studentInformationMapper);

        return studentLineMapper;
    }

    private LineTokenizer createStudentLineTokenizer() {
        DelimitedLineTokenizer studentLineTokenizer = new DelimitedLineTokenizer();
        studentLineTokenizer.setDelimiter(",");
        studentLineTokenizer.setNames(new String[]{"id", "deviceId", "timestamp", "appVersion", "databaseVersion", "batteryStage", "batteryLevel","memoryWired", "memoryActive", "memoryInactive", "memoryFree", "memoryUser", "triggered", "networkStatus","screenBrightness", "screenOn", "timezone", "countryCode", "created", "updated"});
        DefaultFieldSetFactory factory = new DefaultFieldSetFactory();
        factory.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		studentLineTokenizer.setFieldSetFactory(factory);
		studentLineTokenizer.setStrict(false);
        return studentLineTokenizer;
    }

    private FieldSetMapper<SampleDTO> createStudentInformationMapper() {
        BeanWrapperFieldSetMapper<SampleDTO> studentInformationMapper = new BeanWrapperFieldSetMapperCustom<>();
        studentInformationMapper.setTargetType(SampleDTO.class);
        final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        final Map<Class, PropertyEditor> customEditors = Stream.of(
                new AbstractMap.SimpleEntry<>(Date.class, new CustomDateEditor(df, false)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        studentInformationMapper.setCustomEditors(customEditors);
        return studentInformationMapper;
    }

    @Bean
    ItemProcessor<SampleDTO, SampleDTO> csvFileItemProcessor() {
        return new LoggingSampleProcessor();
    }

    @Bean
    ItemWriter<SampleDTO> csvFileDatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        JdbcBatchItemWriter<SampleDTO> databaseItemWriter = new JdbcBatchItemWriter<>();
        databaseItemWriter.setDataSource(dataSource);
        databaseItemWriter.setJdbcTemplate(jdbcTemplate);

        databaseItemWriter.setSql(QUERY_INSERT_STUDENT);

        ItemSqlParameterSourceProvider<SampleDTO> sqlParameterSourceProvider = studentSqlParameterSourceProvider();
        databaseItemWriter.setItemSqlParameterSourceProvider(sqlParameterSourceProvider);

        return databaseItemWriter;
    }

    private ItemSqlParameterSourceProvider<SampleDTO> studentSqlParameterSourceProvider() {
        return new BeanPropertyItemSqlParameterSourceProvider<>();
    }

    @Bean
    Step csvFileToDatabaseStep(ItemReader<SampleDTO> csvFileItemReader,
                               ItemProcessor<SampleDTO, SampleDTO> csvFileItemProcessor,
                               ItemWriter<SampleDTO> csvFileDatabaseItemWriter,
                               StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("csvFileToDatabaseStep")
                .<SampleDTO, SampleDTO>chunk(1)
                .reader(csvFileItemReader)
                .processor(csvFileItemProcessor)
                .writer(csvFileDatabaseItemWriter)
                .build();
    }

    @Bean
    Job csvFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
                             @Qualifier("csvFileToDatabaseStep") Step csvStudentStep) {
        return jobBuilderFactory.get("csvFileToDatabaseJob")
                .incrementer(new RunIdIncrementer())
                .flow(csvStudentStep)
                .end()
                .build();
    }
}
