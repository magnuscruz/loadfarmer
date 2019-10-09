package pt.uc.greenhub.springbatch.csv.in;

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
import org.springframework.batch.core.step.skip.SkipPolicy;
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

import pt.uc.greenhub.springbatch.common.LoggingLocationProcessor;
import pt.uc.greenhub.springbatch.web.controller.LocationDTO;

/**
 * @author Petri Kainulainen
 */
//@Configuration
public class LocationCsvFileToDatabaseJobConfig {

	private static final String PROPERTY_CSV_SOURCE_FILE_PATH = "csv.to.database.job.source.file.path.location";
	private static final String QUERY_INSERT_SAMPLE = "INSERT "
			+ " INTO locations(id, sample_id, provider, created, updated) "
			+ " VALUES (:id, :sampleId, :provider, :created, :updated)";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Bean
	ItemReader<LocationDTO> csvFileItemReader(Environment environment) {
		FlatFileItemReader<LocationDTO> csvFileReader = new FlatFileItemReader<>();
		try {
			csvFileReader.setResource(
					new FileSystemResource(environment.getRequiredProperty(PROPERTY_CSV_SOURCE_FILE_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("erro", e);
		}
		csvFileReader.setLinesToSkip(1);

		LineMapper<LocationDTO> locationLineMapper = createLocationLineMapper();
		csvFileReader.setLineMapper(locationLineMapper);

		return csvFileReader;
	}

	@Bean
	public SkipPolicy fileVerificationSkipper() {
		return new FileVerificationSkipper();
	}

	private LineMapper<LocationDTO> createLocationLineMapper() {
		DefaultLineMapper<LocationDTO> locationLineMapper = new DefaultLineMapper<>();

		LineTokenizer locationLineTokenizer = createLineTokenizer();
		locationLineMapper.setLineTokenizer(locationLineTokenizer);

		FieldSetMapper<LocationDTO> locationInformationMapper = createLocationInformationMapper();
		locationLineMapper.setFieldSetMapper(locationInformationMapper);

		return locationLineMapper;
	}

	private LineTokenizer createLineTokenizer() {
		DelimitedLineTokenizer locationLineTokenizer = new DelimitedLineTokenizer();
		locationLineTokenizer.setDelimiter(",");
		locationLineTokenizer.setNames(new String[] { "id", "sampleId", "provider", "created", "updated" });
		DefaultFieldSetFactory factory = new DefaultFieldSetFactory();
		factory.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		locationLineTokenizer.setFieldSetFactory(factory);
		locationLineTokenizer.setStrict(false);
		return locationLineTokenizer;
	}

	private FieldSetMapper<LocationDTO> createLocationInformationMapper() {
		BeanWrapperFieldSetMapper<LocationDTO> locationInformationMapper = new BeanWrapperFieldSetMapperCustom<>(
				DATE_FORMAT);
		locationInformationMapper.setTargetType(LocationDTO.class);
		final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		final Map<Class, PropertyEditor> customEditors = Stream
				.of(new AbstractMap.SimpleEntry<>(Date.class, new CustomDateEditor(df, false)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		locationInformationMapper.setCustomEditors(customEditors);
		return locationInformationMapper;
	}

	@Bean
	ItemProcessor<LocationDTO, LocationDTO> csvFileItemProcessor() {
		return new LoggingLocationProcessor();
	}

	@Bean
	ItemWriter<LocationDTO> csvFileDatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
		JdbcBatchItemWriter<LocationDTO> databaseItemWriter = new JdbcBatchItemWriter<>();
		databaseItemWriter.setDataSource(dataSource);
		databaseItemWriter.setJdbcTemplate(jdbcTemplate);

		databaseItemWriter.setSql(QUERY_INSERT_SAMPLE);

		ItemSqlParameterSourceProvider<LocationDTO> sqlParameterSourceProvider = locationSqlParameterSourceProvider();
		databaseItemWriter.setItemSqlParameterSourceProvider(sqlParameterSourceProvider);

		return databaseItemWriter;
	}

	private ItemSqlParameterSourceProvider<LocationDTO> locationSqlParameterSourceProvider() {
		return new BeanPropertyItemSqlParameterSourceProvider<>();
	}

	@Bean
	Step locationCsvFileToDatabaseStep(ItemReader<LocationDTO> csvFileItemReader,
			ItemProcessor<LocationDTO, LocationDTO> csvFileItemProcessor,
			ItemWriter<LocationDTO> csvFileDatabaseItemWriter, StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("locationCsvFileToDatabaseStep").<LocationDTO, LocationDTO>chunk(1)
				.reader(csvFileItemReader).faultTolerant().skipPolicy(fileVerificationSkipper())
				.processor(csvFileItemProcessor).writer(csvFileDatabaseItemWriter).faultTolerant()
				.skipPolicy(fileVerificationSkipper()).build();
	}

	@Bean
	Job locationCsvFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
			@Qualifier("locationCsvFileToDatabaseStep") Step csvLocationStep) {
		return jobBuilderFactory.get("locationCsvFileToDatabaseJob").incrementer(new RunIdIncrementer())
				.flow(csvLocationStep).end().build();
	}
}
