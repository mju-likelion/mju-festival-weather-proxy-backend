package org.mju_likelion.weather_proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeatherProxyApplication {

  public static void main(String[] args) {
    SpringApplication.run(WeatherProxyApplication.class, args);
  }
}
