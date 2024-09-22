package org.mju_likelion.weather_proxy;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeatherController {

  private final WeatherService weatherService;

  @GetMapping("/weather")
  public ResponseEntity<Object> getWeather() {
    return ResponseEntity.ok(weatherService.getWeather());
  }
}
