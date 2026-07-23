package org.inventory_tracker.integration.cams.service;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cams")
public class CamsConfiguration {

    private String baseUrl;
    private String webhookSecret;
}
