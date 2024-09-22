package org.mju_likelion.weather_proxy;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class WeatherApiArgsProvider {

  private final String pageNo = "1";
  private final String numOfRows = "60";
  private final String dataType = "JSON";
  private final String nx = "63";
  private final String ny = "119";
  @Value("${weather.api.key}")
  private String weatherApiKey;

  public String getArgs(final String baseDate, final String baseTime) {
    return String.format(
        "?ServiceKey=%s&pageNo=%s&numOfRows=%s&dataType=%s&base_date=%s&base_time=%s&nx=%s&ny=%s",
        URLEncoder.encode(weatherApiKey, StandardCharsets.UTF_8), pageNo, numOfRows, dataType,
        baseDate, baseTime, nx, ny);
  }
}
