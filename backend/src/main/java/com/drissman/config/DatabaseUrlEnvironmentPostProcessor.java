package com.drissman.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Automatiquement convertit les URLs de bases de données cloud (Railway, Render, Heroku)
 * au format JDBC (jdbc:postgresql://...) et R2DBC (r2dbc:postgresql://...).
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dbUrl = environment.getProperty("DATABASE_URL");
        String dsUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        String r2dbcUrl = environment.getProperty("SPRING_R2DBC_URL");

        String rawUrl = (dsUrl != null && !dsUrl.isEmpty() && !dsUrl.contains("127.0.0.1")) ? dsUrl : dbUrl;

        if (rawUrl != null && !rawUrl.isEmpty()) {
            Map<String, Object> map = new HashMap<>();

            if (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://")) {
                try {
                    String cleanUrl = rawUrl.replaceFirst("^postgres://", "postgresql://");
                    URI uri = URI.create(cleanUrl);

                    String host = uri.getHost();
                    int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                    String path = uri.getPath();

                    String userInfo = uri.getUserInfo();
                    if (userInfo != null && userInfo.contains(":")) {
                        String[] parts = userInfo.split(":", 2);
                        map.put("spring.datasource.username", parts[0]);
                        map.put("spring.datasource.password", parts[1]);
                        map.put("spring.r2dbc.username", parts[0]);
                        map.put("spring.r2dbc.password", parts[1]);
                    }

                    String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                    String r2dbcCleanUrl = "r2dbc:postgresql://" + host + ":" + port + path;

                    map.put("spring.datasource.url", jdbcUrl);
                    map.put("spring.liquibase.url", jdbcUrl);
                    map.put("spring.r2dbc.url", r2dbcCleanUrl);

                } catch (Exception ignored) {
                }
            } else if (rawUrl.startsWith("jdbc:postgresql://")) {
                map.put("spring.datasource.url", rawUrl);
                map.put("spring.liquibase.url", rawUrl);

                String r2dbcCleanUrl = rawUrl.replace("jdbc:", "r2dbc:");
                if (r2dbcUrl == null || r2dbcUrl.isEmpty() || r2dbcUrl.contains("127.0.0.1")) {
                    map.put("spring.r2dbc.url", r2dbcCleanUrl);
                }
            }

            if (!map.isEmpty()) {
                environment.getPropertySources().addFirst(new MapPropertySource("cloudDatabaseUrlProperties", map));
            }
        }
    }
}
