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

import pt.uc.greenhub.springbatch.common.LoggingSettingProcessor;
import pt.uc.greenhub.springbatch.web.controller.SettingDTO;

/**
 * @author Petri Kainulainen
 */
//@Configuration
public class SettingCsvFileToDatabaseJobConfig {

	private static final String PROPERTY_CSV_SOURCE_FILE_PATH = "csv.to.database.job.source.file.path.setting";
	private static final String QUERY_INSERT_SAMPLE = "INSERT "
			+ " INTO settings(id, sample_id, bluetooth_enabled, location_enabled, power_saver_enabled, flash_light_enabled, nfc_enabled, unknown_source, developer_mode, created, updated) "
			+ " VALUES (:id, :sampleId, :bluetoothEnabled, :locationEnabled, :powerSaverEnabled, :flashLightEnabled, :nfcEnabled, :unknownSource, :developerMode, :created, :updated)";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Bean
	ItemReader<SettingDTO> csvFileItemReader(Environment environment) {
		FlatFileItemReader<SettingDTO> csvFileReader = new FlatFileItemReader<>();
		try {
			csvFileReader.setResource(
					new FileSystemResource(environment.getRequiredProperty(PROPERTY_CSV_SOURCE_FILE_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("erro", e);
		}
		csvFileReader.setLinesToSkip(1);

		LineMapper<SettingDTO> settingLineMapper = createSettingLineMapper();
		csvFileReader.setLineMapper(settingLineMapper);

		return csvFileReader;
	}

	@Bean
	public SkipPolicy fileVerificationSkipper() {
		return new FileVerificationSkipper();
	}

	private LineMapper<SettingDTO> createSettingLineMapper() {
		DefaultLineMapper<SettingDTO> settingLineMapper = new DefaultLineMapper<>();

		LineTokenizer settingLineTokenizer = createLineTokenizer();
		settingLineMapper.setLineTokenizer(settingLineTokenizer);

		FieldSetMapper<SettingDTO> settingInformationMapper = createSettingInformationMapper();
		settingLineMapper.setFieldSetMapper(settingInformationMapper);

		return settingLineMapper;
	}

	private LineTokenizer createLineTokenizer() {
		DelimitedLineTokenizer settingLineTokenizer = new DelimitedLineTokenizer();
		settingLineTokenizer.setDelimiter(",");
		settingLineTokenizer.setNames(new String[] { "id", "sampleId", "bluetoothEnabled", "locationEnabled", "powerSaverEnabled", "flashLightEnabled",
				"nfcEnabled", "unknownSource", "developerMode", "created", "updated" });
		DefaultFieldSetFactory factory = new DefaultFieldSetFactory();
		factory.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		settingLineTokenizer.setFieldSetFactory(factory);
		settingLineTokenizer.setStrict(false);
		return settingLineTokenizer;
	}

	private FieldSetMapper<SettingDTO> createSettingInformationMapper() {
		BeanWrapperFieldSetMapper<SettingDTO> settingInformationMapper = new BeanWrapperFieldSetMapperCustom<>(
				DATE_FORMAT);
		settingInformationMapper.setTargetType(SettingDTO.class);
		final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		final Map<Class, PropertyEditor> customEditors = Stream
				.of(new AbstractMap.SimpleEntry<>(Date.class, new CustomDateEditor(df, false)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		settingInformationMapper.setCustomEditors(customEditors);
		return settingInformationMapper;
	}

	@Bean
	ItemProcessor<SettingDTO, SettingDTO> csvFileItemProcessor() {
		return new LoggingSettingProcessor();
	}

	@Bean
	ItemWriter<SettingDTO> csvFileDatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
		JdbcBatchItemWriter<SettingDTO> databaseItemWriter = new JdbcBatchItemWriter<>();
		databaseItemWriter.setDataSource(dataSource);
		databaseItemWriter.setJdbcTemplate(jdbcTemplate);

		databaseItemWriter.setSql(QUERY_INSERT_SAMPLE);

		ItemSqlParameterSourceProvider<SettingDTO> sqlParameterSourceProvider = settingSqlParameterSourceProvider();
		databaseItemWriter.setItemSqlParameterSourceProvider(sqlParameterSourceProvider);

		return databaseItemWriter;
	}

	private ItemSqlParameterSourceProvider<SettingDTO> settingSqlParameterSourceProvider() {
		return new BeanPropertyItemSqlParameterSourceProvider<>();
	}

	@Bean
	Step settingCsvFileToDatabaseStep(ItemReader<SettingDTO> csvFileItemReader,
			ItemProcessor<SettingDTO, SettingDTO> csvFileItemProcessor,
			ItemWriter<SettingDTO> csvFileDatabaseItemWriter, StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("settingCsvFileToDatabaseStep").<SettingDTO, SettingDTO>chunk(1)
				.reader(csvFileItemReader).faultTolerant().skipPolicy(fileVerificationSkipper())
				.processor(csvFileItemProcessor).writer(csvFileDatabaseItemWriter).faultTolerant()
				.skipPolicy(fileVerificationSkipper()).build();
	}

	@Bean
	Job settingCsvFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
			@Qualifier("settingCsvFileToDatabaseStep") Step csvSettingStep) {
		return jobBuilderFactory.get("settingCsvFileToDatabaseJob").incrementer(new RunIdIncrementer())
				.flow(csvSettingStep).end().build();
	}
}
