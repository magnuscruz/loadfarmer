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

import pt.uc.greenhub.springbatch.common.LoggingCpuProcessor;
import pt.uc.greenhub.springbatch.web.controller.CpuDTO;

/**
 * @author Petri Kainulainen
 */
//@Configuration
public class CpuCsvFileToDatabaseJobConfig {

	private static final String PROPERTY_CSV_SOURCE_FILE_PATH = "csv.to.database.job.source.file.path.cpu";
	private static final String QUERY_INSERT_SAMPLE = "INSERT "
			+ " INTO cpus(id, sample_id, usage, up_time, sleep_time, created, updated) "
			+ " VALUES (:id, :sampleId, :usage, :upTime, :sleepTime, :created, :updated)";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Bean
	ItemReader<CpuDTO> csvFileItemReader(Environment environment) {
		FlatFileItemReader<CpuDTO> csvFileReader = new FlatFileItemReader<>();
		try {
			csvFileReader.setResource(
					new FileSystemResource(environment.getRequiredProperty(PROPERTY_CSV_SOURCE_FILE_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("erro", e);
		}
		csvFileReader.setLinesToSkip(1);

		LineMapper<CpuDTO> cpuLineMapper = createCpuLineMapper();
		csvFileReader.setLineMapper(cpuLineMapper);

		return csvFileReader;
	}

	@Bean
	public SkipPolicy fileVerificationSkipper() {
		return new FileVerificationSkipper();
	}

	private LineMapper<CpuDTO> createCpuLineMapper() {
		DefaultLineMapper<CpuDTO> cpuLineMapper = new DefaultLineMapper<>();

		LineTokenizer cpuLineTokenizer = createLineTokenizer();
		cpuLineMapper.setLineTokenizer(cpuLineTokenizer);

		FieldSetMapper<CpuDTO> cpuInformationMapper = createCpuInformationMapper();
		cpuLineMapper.setFieldSetMapper(cpuInformationMapper);

		return cpuLineMapper;
	}

	private LineTokenizer createLineTokenizer() {
		DelimitedLineTokenizer cpuLineTokenizer = new DelimitedLineTokenizer();
		cpuLineTokenizer.setDelimiter(",");
		cpuLineTokenizer.setNames(new String[] { "id", "sampleId", "usage", "upTime", "sleepTime", "created", "updated" });
		DefaultFieldSetFactory factory = new DefaultFieldSetFactory();
		factory.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		cpuLineTokenizer.setFieldSetFactory(factory);
		cpuLineTokenizer.setStrict(false);
		return cpuLineTokenizer;
	}

	private FieldSetMapper<CpuDTO> createCpuInformationMapper() {
		BeanWrapperFieldSetMapper<CpuDTO> cpuInformationMapper = new BeanWrapperFieldSetMapperCustom<>(
				DATE_FORMAT);
		cpuInformationMapper.setTargetType(CpuDTO.class);
		final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		final Map<Class, PropertyEditor> customEditors = Stream
				.of(new AbstractMap.SimpleEntry<>(Date.class, new CustomDateEditor(df, false)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		cpuInformationMapper.setCustomEditors(customEditors);
		return cpuInformationMapper;
	}

	@Bean
	ItemProcessor<CpuDTO, CpuDTO> csvFileItemProcessor() {
		return new LoggingCpuProcessor();
	}

	@Bean
	ItemWriter<CpuDTO> csvFileDatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
		JdbcBatchItemWriter<CpuDTO> databaseItemWriter = new JdbcBatchItemWriter<>();
		databaseItemWriter.setDataSource(dataSource);
		databaseItemWriter.setJdbcTemplate(jdbcTemplate);

		databaseItemWriter.setSql(QUERY_INSERT_SAMPLE);

		ItemSqlParameterSourceProvider<CpuDTO> sqlParameterSourceProvider = cpuSqlParameterSourceProvider();
		databaseItemWriter.setItemSqlParameterSourceProvider(sqlParameterSourceProvider);

		return databaseItemWriter;
	}

	private ItemSqlParameterSourceProvider<CpuDTO> cpuSqlParameterSourceProvider() {
		return new BeanPropertyItemSqlParameterSourceProvider<>();
	}

	@Bean
	Step cpuCsvFileToDatabaseStep(ItemReader<CpuDTO> csvFileItemReader,
			ItemProcessor<CpuDTO, CpuDTO> csvFileItemProcessor,
			ItemWriter<CpuDTO> csvFileDatabaseItemWriter, StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("cpuCsvFileToDatabaseStep").<CpuDTO, CpuDTO>chunk(1)
				.reader(csvFileItemReader).faultTolerant().skipPolicy(fileVerificationSkipper())
				.processor(csvFileItemProcessor).writer(csvFileDatabaseItemWriter).faultTolerant()
				.skipPolicy(fileVerificationSkipper()).build();
	}

	@Bean
	Job cpuCsvFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
			@Qualifier("cpuCsvFileToDatabaseStep") Step csvCpuStep) {
		return jobBuilderFactory.get("cpuCsvFileToDatabaseJob").incrementer(new RunIdIncrementer())
				.flow(csvCpuStep).end().build();
	}
}
