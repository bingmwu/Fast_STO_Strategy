package com.bingmwu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@EnableAutoConfiguration 
@EnableEntityLinks
@EnableConfigurationProperties
@EnableSpringDataWebSupport
@ComponentScan("com.bingmwu")
public class ApplicationMain {

	public static final String BASE_RELATIVE_API_URL = "/edge";
	public static final String BASE_MOCK_RELATIVE_API_URL = "/mock" + BASE_RELATIVE_API_URL;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationMain.class, args);
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		final MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
		methodValidationPostProcessor.setValidator(validator());

		return methodValidationPostProcessor;
	}

	@Bean
	public LocalValidatorFactoryBean validator() {
		final LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		return localValidatorFactoryBean;
	}
}
