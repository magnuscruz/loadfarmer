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

import pt.uc.greenhub.springbatch.common.LoggingNetworkProcessor;
import pt.uc.greenhub.springbatch.web.controller.NetworkDTO;

/**
 * @author Petri Kainulainen
 */
//@Configuration
public class NetworkCsvFileToDatabaseJobConfig {

	private static final String PROPERTY_CSV_SOURCE_FILE_PATH = "csv.to.database.job.source.file.path.network";
	private static final String QUERY_INSERT_SAMPLE = "INSERT "
			+ " INTO networks(id, sample_id, network_type, mobile_network_type, mobile_data_status, mobile_data_activity, roaming_enabled, wifi_status, wifi_signal, wifi_speed, wifi_ap_status, network_operator, sim_operator, mcc, mnc, created, updated) "
			+ " VALUES (:id, :sampleId, :networkType, :mobileNetworkType, :mobileDataStatus, :mobileDataActivity, :roamingEnabled, :wifiStatus, :wifiSignal, :wifiSpeed, :wifiApStatus, :networkOperator, :simOperator, :mcc, :mnc, :created, :updated)";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Bean
	ItemReader<NetworkDTO> csvFileItemReader(Environment environment) {
		FlatFileItemReader<NetworkDTO> csvFileReader = new FlatFileItemReader<>();
		try {
			csvFileReader.setResource(
					new FileSystemResource(environment.getRequiredProperty(PROPERTY_CSV_SOURCE_FILE_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("erro", e);
		}
		csvFileReader.setLinesToSkip(1);

		LineMapper<NetworkDTO> networkLineMapper = createNetworkLineMapper();
		csvFileReader.setLineMapper(networkLineMapper);

		return csvFileReader;
	}

	@Bean
	public SkipPolicy fileVerificationSkipper() {
		return new FileVerificationSkipper();
	}

	private LineMapper<NetworkDTO> createNetworkLineMapper() {
		DefaultLineMapper<NetworkDTO> networkLineMapper = new DefaultLineMapper<>();

		LineTokenizer networkLineTokenizer = createLineTokenizer();
		networkLineMapper.setLineTokenizer(networkLineTokenizer);

		FieldSetMapper<NetworkDTO> networkInformationMapper = createNetworkInformationMapper();
		networkLineMapper.setFieldSetMapper(networkInformationMapper);

		return networkLineMapper;
	}

	private LineTokenizer createLineTokenizer() {
		DelimitedLineTokenizer networkLineTokenizer = new DelimitedLineTokenizer();
		networkLineTokenizer.setDelimiter(",");
		networkLineTokenizer.setNames(new String[] { "id", "sampleId", "networkType", "mobileNetworkType", "mobileDataStatus", "mobileDataActivity",
				"roamingEnabled", "wifiStatus", "wifiSignal", "wifiSpeed", "wifiApStatus", "networkOperator", "simOperator", "mcc", "mnc", "created", "updated" });
		DefaultFieldSetFactory factory = new DefaultFieldSetFactory();
		factory.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		networkLineTokenizer.setFieldSetFactory(factory);
		networkLineTokenizer.setStrict(false);
		return networkLineTokenizer;
	}

	private FieldSetMapper<NetworkDTO> createNetworkInformationMapper() {
		BeanWrapperFieldSetMapper<NetworkDTO> networkInformationMapper = new BeanWrapperFieldSetMapperCustom<>(
				DATE_FORMAT);
		networkInformationMapper.setTargetType(NetworkDTO.class);
		final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		final Map<Class, PropertyEditor> customEditors = Stream
				.of(new AbstractMap.SimpleEntry<>(Date.class, new CustomDateEditor(df, false)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		networkInformationMapper.setCustomEditors(customEditors);
		return networkInformationMapper;
	}

	@Bean
	ItemProcessor<NetworkDTO, NetworkDTO> csvFileItemProcessor() {
		return new LoggingNetworkProcessor();
	}

	@Bean
	ItemWriter<NetworkDTO> csvFileDatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
		JdbcBatchItemWriter<NetworkDTO> databaseItemWriter = new JdbcBatchItemWriter<>();
		databaseItemWriter.setDataSource(dataSource);
		databaseItemWriter.setJdbcTemplate(jdbcTemplate);

		databaseItemWriter.setSql(QUERY_INSERT_SAMPLE);

		ItemSqlParameterSourceProvider<NetworkDTO> sqlParameterSourceProvider = networkSqlParameterSourceProvider();
		databaseItemWriter.setItemSqlParameterSourceProvider(sqlParameterSourceProvider);

		return databaseItemWriter;
	}

	private ItemSqlParameterSourceProvider<NetworkDTO> networkSqlParameterSourceProvider() {
		return new BeanPropertyItemSqlParameterSourceProvider<>();
	}

	@Bean
	Step networkCsvFileToDatabaseStep(ItemReader<NetworkDTO> csvFileItemReader,
			ItemProcessor<NetworkDTO, NetworkDTO> csvFileItemProcessor,
			ItemWriter<NetworkDTO> csvFileDatabaseItemWriter, StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("networkCsvFileToDatabaseStep").<NetworkDTO, NetworkDTO>chunk(1)
				.reader(csvFileItemReader).faultTolerant().skipPolicy(fileVerificationSkipper())
				.processor(csvFileItemProcessor).writer(csvFileDatabaseItemWriter).faultTolerant()
				.skipPolicy(fileVerificationSkipper()).build();
	}

	@Bean
	Job networkCsvFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
			@Qualifier("networkCsvFileToDatabaseStep") Step csvNetworkStep) {
		return jobBuilderFactory.get("networkCsvFileToDatabaseJob").incrementer(new RunIdIncrementer())
				.flow(csvNetworkStep).end().build();
	}
}
