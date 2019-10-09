package pt.uc.greenhub.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Petri Kainulainen
 */
@SpringBootApplication
//@EnableBatchProcessing
//@EnableScheduling
@EnableWebMvc
@ComponentScan({ "pt.uc.greenhub.springbatch.web.controller" })
public class SpringBatchExampleApplication {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchExampleApplication.class, args);
    }
}
