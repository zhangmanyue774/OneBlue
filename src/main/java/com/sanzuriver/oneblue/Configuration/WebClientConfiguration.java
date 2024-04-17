package com.sanzuriver.oneblue.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebClientConfiguration {
    @Bean("qqMusicRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
                    HttpHeaders headers = request.getHeaders();
                    headers.add("User-Agent", "QQ音乐/73222 CFNetwork/1406.0.2 Darwin/22.4.0");
                    headers.add("Accept", "*/*");
                    headers.add("Accept-Language", "zh-CN,zh-Hans;q=0.9");
                    headers.add("Referer", "http://y.qq.com");
                    headers.add("Content-Type", "application/json; charset=UTF-8");
                    headers.add("Cookie", "");
                    return execution.execute(request, body);
                });
        return restTemplate;
    }
}
