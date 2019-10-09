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

import pt.uc.greenhub.springbatch.common.LoggingProcessProcessor;
import pt.uc.greenhub.springbatch.web.controller.ProcessDTO;

/**
 * @author Petri Kainulainen
 */
//@Configuration
public class ProcessCsvFileToDatabaseJobConfig {

	private static final String PROPERTY_CSV_SOURCE_FILE_PATH = "csv.to.database.job.source.file.path.process";
	private static final String QUERY_INSERT_SAMPLE = "INSERT " +
			" INTO processes(id, sample_id, process_id, name, application_label, is_system_app, importance, version_name, version_code, installation_package, created, updated) " +
			" VALUES (:id, :sampleId, :processId, :name, :applicationLabel, :isSystemApp, :importance, :versionName, :versionCode, :installationPackage, :created, :updated)";
	private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

	@Bean
	ItemReader<ProcessDTO> csvFileItemReader(Environment environment) {
		FlatFileItemReader<ProcessDTO> csvFileReader = new FlatFileItemReader<>();
		try {
			csvFileReader.setResource(
					new FileSystemResource(environment.getRequiredProperty(PROPERTY_CSV_SOURCE_FILE_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("erro", e);
		}
		csvFileReader.setLinesToSkip(1);

		LineMapper<ProcessDTO> processLineMapper = createStudentLineMapper();
		csvFileReader.setLineMapper(processLineMapper);

		return csvFileReader;
	}

	@Bean
	public SkipPolicy fileVerificationSkipper() {
		return new FileVerificationSkipper();
	}

	private LineMapper<ProcessDTO> createStudentLineMapper() {
		DefaultLineMapper<ProcessDTO> processLineMapper = new DefaultLineMapper<>();

		LineTokenizer processLineTokenizer = createLineTokenizer();
		processLineMapper.setLineTokenizer(processLineTokenizer);

		FieldSetMapper<ProcessDTO> processInformationMapper = createStudentInformationMapper();
		processLineMapper.setFieldSetMapper(processInformationMapper);

		return processLineMapper;
	}

	private LineTokenizer createLineTokenizer() {
		DelimitedLineTokenizer processLineTokenizer = new DelimitedLineTokenizer();
		processLineTokenizer.setDelimiter(",");

		processLineTokenizer
				.setNames(new String[] { "id", "sampleId", "processId", "name", "applicationLabel", "isSystemApp",
						"importance", "versionName", "versionCode", "installationPackage", "created", "updated" });
		DefaultFieldSetFactory factory = new DefaultFieldSetFactory();
		factory.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		processLineTokenizer.setFieldSetFactory(factory);
		processLineTokenizer.setStrict(false);
		return processLineTokenizer;
	}

	private FieldSetMapper<ProcessDTO> createStudentInformationMapper() {
		BeanWrapperFieldSetMapper<ProcessDTO> processInformationMapper = new BeanWrapperFieldSetMapperCustom<>(DATE_FORMAT);
		processInformationMapper.setTargetType(ProcessDTO.class);
		final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		final Map<Class, PropertyEditor> customEditors = Stream
				.of(new AbstractMap.SimpleEntry<>(Date.class, new CustomDateEditor(df, false)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		processInformationMapper.setCustomEditors(customEditors);
		return processInformationMapper;
	}

	@Bean
	ItemProcessor<ProcessDTO, ProcessDTO> csvFileItemProcessor() {
		return new LoggingProcessProcessor();
	}

	@Bean
	ItemWriter<ProcessDTO> csvFileDatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
		JdbcBatchItemWriter<ProcessDTO> databaseItemWriter = new JdbcBatchItemWriter<>();
		databaseItemWriter.setDataSource(dataSource);
		databaseItemWriter.setJdbcTemplate(jdbcTemplate);

		databaseItemWriter.setSql(QUERY_INSERT_SAMPLE);

		ItemSqlParameterSourceProvider<ProcessDTO> sqlParameterSourceProvider = processSqlParameterSourceProvider();
		databaseItemWriter.setItemSqlParameterSourceProvider(sqlParameterSourceProvider);

		return databaseItemWriter;
	}

	private ItemSqlParameterSourceProvider<ProcessDTO> processSqlParameterSourceProvider() {
		return new BeanPropertyItemSqlParameterSourceProvider<>();
	}

	@Bean
	Step processCsvFileToDatabaseStep(ItemReader<ProcessDTO> csvFileItemReader,
			ItemProcessor<ProcessDTO, ProcessDTO> csvFileItemProcessor,
			ItemWriter<ProcessDTO> csvFileDatabaseItemWriter, StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("processCsvFileToDatabaseStep").<ProcessDTO, ProcessDTO>chunk(1)
				.reader(csvFileItemReader).faultTolerant().skipPolicy(fileVerificationSkipper())
				.processor(csvFileItemProcessor).writer(csvFileDatabaseItemWriter).faultTolerant()
				.skipPolicy(fileVerificationSkipper()).build();
	}

	@Bean
	Job processCsvFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
			@Qualifier("processCsvFileToDatabaseStep") Step csvStudentStep) {
		return jobBuilderFactory.get("processCsvFileToDatabaseJob").incrementer(new RunIdIncrementer()).flow(csvStudentStep)
				.end().build();
	}
}
