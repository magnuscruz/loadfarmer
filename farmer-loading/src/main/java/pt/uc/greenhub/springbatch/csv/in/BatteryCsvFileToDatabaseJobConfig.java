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

import pt.uc.greenhub.springbatch.common.LoggingBatteryProcessor;
import pt.uc.greenhub.springbatch.web.controller.BatteryDTO;

/**
 * @author Petri Kainulainen
 */
//@Configuration
public class BatteryCsvFileToDatabaseJobConfig {

	private static final String PROPERTY_CSV_SOURCE_FILE_PATH = "csv.to.database.job.source.file.path.battery";
	private static final String QUERY_INSERT_SAMPLE = "INSERT "
			+ " INTO batteries(id, sample_id, changer, health, voltage, temperature, capacity, change_counter, current_average, current_now, energy_counter, created_at, updated) "
			+ " VALUES (:id, :sampleId, :changer, :health, :voltage, :temperature, :capacity, :changeCounter, :currentAverage, :currentNow, :energyCounter, :createdAt, :updated)";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Bean
	ItemReader<BatteryDTO> csvFileItemReader(Environment environment) {
		FlatFileItemReader<BatteryDTO> csvFileReader = new FlatFileItemReader<>();
		try {
			csvFileReader.setResource(
					new FileSystemResource(environment.getRequiredProperty(PROPERTY_CSV_SOURCE_FILE_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("erro", e);
		}
		csvFileReader.setLinesToSkip(1);

		LineMapper<BatteryDTO> batteryLineMapper = createBatteryLineMapper();
		csvFileReader.setLineMapper(batteryLineMapper);

		return csvFileReader;
	}

	@Bean
	public SkipPolicy fileVerificationSkipper() {
		return new FileVerificationSkipper();
	}

	private LineMapper<BatteryDTO> createBatteryLineMapper() {
		DefaultLineMapper<BatteryDTO> batteryLineMapper = new DefaultLineMapper<>();

		LineTokenizer batteryLineTokenizer = createLineTokenizer();
		batteryLineMapper.setLineTokenizer(batteryLineTokenizer);

		FieldSetMapper<BatteryDTO> batteryInformationMapper = createBatteryInformationMapper();
		batteryLineMapper.setFieldSetMapper(batteryInformationMapper);

		return batteryLineMapper;
	}

	private LineTokenizer createLineTokenizer() {
		DelimitedLineTokenizer batteryLineTokenizer = new DelimitedLineTokenizer();
		batteryLineTokenizer.setDelimiter(",");
		batteryLineTokenizer.setNames(new String[] { "id", "sampleId", "changer", "health", "voltage", "temperature",
				"capacity", "changeCounter", "currentAverage", "currentNow", "energyCounter", "createdAt", "updated" });
		DefaultFieldSetFactory factory = new DefaultFieldSetFactory();
		factory.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		batteryLineTokenizer.setFieldSetFactory(factory);
		batteryLineTokenizer.setStrict(false);
		return batteryLineTokenizer;
	}

	private FieldSetMapper<BatteryDTO> createBatteryInformationMapper() {
		BeanWrapperFieldSetMapper<BatteryDTO> batteryInformationMapper = new BeanWrapperFieldSetMapperCustom<>(
				DATE_FORMAT);
		batteryInformationMapper.setTargetType(BatteryDTO.class);
		final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		final Map<Class, PropertyEditor> customEditors = Stream
				.of(new AbstractMap.SimpleEntry<>(Date.class, new CustomDateEditor(df, false)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		batteryInformationMapper.setCustomEditors(customEditors);
		return batteryInformationMapper;
	}

	@Bean
	ItemProcessor<BatteryDTO, BatteryDTO> csvFileItemProcessor() {
		return new LoggingBatteryProcessor();
	}

	@Bean
	ItemWriter<BatteryDTO> csvFileDatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
		JdbcBatchItemWriter<BatteryDTO> databaseItemWriter = new JdbcBatchItemWriter<>();
		databaseItemWriter.setDataSource(dataSource);
		databaseItemWriter.setJdbcTemplate(jdbcTemplate);

		databaseItemWriter.setSql(QUERY_INSERT_SAMPLE);

		ItemSqlParameterSourceProvider<BatteryDTO> sqlParameterSourceProvider = batterySqlParameterSourceProvider();
		databaseItemWriter.setItemSqlParameterSourceProvider(sqlParameterSourceProvider);

		return databaseItemWriter;
	}

	private ItemSqlParameterSourceProvider<BatteryDTO> batterySqlParameterSourceProvider() {
		return new BeanPropertyItemSqlParameterSourceProvider<>();
	}

	@Bean
	Step batteryCsvFileToDatabaseStep(ItemReader<BatteryDTO> csvFileItemReader,
			ItemProcessor<BatteryDTO, BatteryDTO> csvFileItemProcessor,
			ItemWriter<BatteryDTO> csvFileDatabaseItemWriter, StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("batteryCsvFileToDatabaseStep").<BatteryDTO, BatteryDTO>chunk(1)
				.reader(csvFileItemReader).faultTolerant().skipPolicy(fileVerificationSkipper())
				.processor(csvFileItemProcessor).writer(csvFileDatabaseItemWriter).faultTolerant()
				.skipPolicy(fileVerificationSkipper()).build();
	}

	@Bean
	Job batteryCsvFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
			@Qualifier("batteryCsvFileToDatabaseStep") Step csvBatteryStep) {
		return jobBuilderFactory.get("batteryCsvFileToDatabaseJob").incrementer(new RunIdIncrementer())
				.flow(csvBatteryStep).end().build();
	}
}
