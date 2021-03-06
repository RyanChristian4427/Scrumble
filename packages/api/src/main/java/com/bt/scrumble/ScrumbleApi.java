package com.bt.scrumble;

import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableOAuth2Client
@EnableTransactionManagement
@EnableConfigurationProperties(ScrumbleConfig.class)
public class ScrumbleApi extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(ScrumbleApi.class, args);
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public FlywayMigrationStrategy cleanAndMigrateStrategy() {
    return Flyway::migrate;
  }
}
