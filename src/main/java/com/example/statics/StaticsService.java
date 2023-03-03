package com.example.statics;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayMetricsAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@SpringBootApplication(scanBasePackageClasses = StaticsService.class, exclude = { GatewayAutoConfiguration.class,
    GatewayMetricsAutoConfiguration.class })
@EnableWebFluxSecurity
public class StaticsService
{
  public static void main(final String[] args)
  {
    new SpringApplicationBuilder(StaticsService.class).profiles("statics").run(args);
  }

  @Bean
  SecurityWebFilterChain configure(final ServerHttpSecurity httpSecurity)
  {
    return httpSecurity.authorizeExchange().anyExchange().permitAll().and().csrf().disable().build();
  }
}
