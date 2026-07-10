package com.drissman.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kernel")
@Data
public class KernelProperties {
    private String baseUrl;
    private String clientId;
    private String apiKey;
    private String tenantId;
}
