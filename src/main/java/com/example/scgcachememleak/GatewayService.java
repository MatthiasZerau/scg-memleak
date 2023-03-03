package com.example.scgcachememleak;

import java.time.Duration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.unit.DataSize;

@SpringBootApplication
public class GatewayService {

  public static void main(String[] args) {
    System.setProperty("io.netty.leakDetection.level", "PARANOID");
    new SpringApplicationBuilder(GatewayService.class).profiles("scg").run(args);
  }
  
  @Bean
  public RouteLocator google(final RouteLocatorBuilder aRouteBuilder) {
    return aRouteBuilder.routes()//
        .route("memleak", r -> r.method(HttpMethod.GET)//
            .and().path("/x/**")//
            .filters(filterspec -> filterspec.localResponseCache(Duration.ofMinutes(5), DataSize.ofMegabytes(10))
                .stripPrefix(1))//
            .uri("http://localhost:8282"))//
        .build();
  }
  
  @Bean
  SecurityWebFilterChain configure(final ServerHttpSecurity aHttpSecurity)
  {
    return aHttpSecurity.authorizeExchange().anyExchange().permitAll().and().csrf().disable().build();
  }
}
