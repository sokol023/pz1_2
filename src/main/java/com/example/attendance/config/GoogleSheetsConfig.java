package com.example.attendance.config;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleSheetsConfig {

    @Value("${google.sheets.api-key:}")
    private String apiKey;

    @Value("${spring.application.name:pz1_sokol}")
    private String applicationName;

    @Bean
    public Sheets sheetsService() {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        HttpRequestInitializer requestInitializer = httpRequest -> {
            httpRequest.setConnectTimeout(5000); // 5 секунд таймаут з'єднання
            httpRequest.setReadTimeout(5000);    // 5 секунд таймаут зчитування
        };

        return new Sheets.Builder(httpTransport, jsonFactory, requestInitializer)
                .setApplicationName(applicationName)
                .build();
    }
}