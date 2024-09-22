package org.mju_likelion.weather_proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NavigableSet;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WeatherService {

  private final RestTemplate restTemplate;
  private final NavigableSet<LocalTime> scheduleBaseTimes;
  private final WeatherApiArgsProvider weatherApiArgsProvider;
  private Object weatherApiResponse;

  @Value("${weather.api.url}")
  private String weatherApiUrl;

  public WeatherService(RestTemplate restTemplate, WeatherApiArgsProvider weatherApiArgsProvider) {
    this.restTemplate = restTemplate;
    this.weatherApiArgsProvider = weatherApiArgsProvider;
    this.scheduleBaseTimes = createScheduleBaseTimes();
  }

  private NavigableSet<LocalTime> createScheduleBaseTimes() {
    NavigableSet<LocalTime> times = new TreeSet<>();
    for (int i = 0; i < 24; i++) {
      times.add(LocalTime.of(i, 30));
    }
    return times;
  }

  @PostConstruct
  public void initWeather() {
    updateWeatherWithClosestTime();
  }

  private void updateWeatherWithClosestTime() {
    LocalDateTime previousWeatherTime = getPreviousWeatherTime();
    updateWeather(previousWeatherTime.toLocalDate(), previousWeatherTime.toLocalTime());
  }

  private LocalDateTime getPreviousWeatherTime() {
    LocalTime nowTime = LocalTime.now();
    LocalDate nowDate = LocalDate.now();
    LocalTime previousTime = scheduleBaseTimes.floor(nowTime);

    if (previousTime == null) {
      previousTime = scheduleBaseTimes.last();
      return LocalDateTime.of(nowDate.minusDays(1), previousTime);
    }
    return LocalDateTime.of(nowDate, previousTime);
  }

  public Object getWeather() {
    return weatherApiResponse;
  }

  @Scheduled(fixedRate = 58000)
  public void weatherScheduler() {
    LocalTime nowTime = LocalTime.now();
    LocalDate nowDate = LocalDate.now();
    if (scheduleBaseTimes.contains(nowTime)) {
      log.info("Update weather data");
      updateWeather(nowDate, nowTime);
    }
  }

  public void updateWeather(final LocalDate date, final LocalTime time) {
    System.out.println(date);
    System.out.println(time);
    String baseDate = formatDate(date);
    String baseTime = formatTime(time);
    String argString = weatherApiArgsProvider.getArgs(baseDate, baseTime);
    URI uri = URI.create(weatherApiUrl + argString);

    ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
    parseWeatherApiResponse(response);
  }

  private void parseWeatherApiResponse(final ResponseEntity<String> response) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      this.weatherApiResponse = mapper.readValue(response.getBody(), Object.class);
    } catch (JsonProcessingException ignored) {
    }
  }

  private String formatDate(LocalDate date) {
    return date.toString().replace("-", "");
  }

  private String formatTime(LocalTime time) {
    return time.toString().replace(":", "").substring(0, 4);
  }
}
