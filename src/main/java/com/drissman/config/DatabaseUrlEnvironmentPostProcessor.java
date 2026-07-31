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
 * aux formats JDBC (jdbc:postgresql://...) et R2DBC (r2dbc:postgresql://...)
 * en extrayant proprement les identifiants pour éviter l'UnknownHostException.
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dbUrl = System.getenv("DATABASE_URL");
        String dsUrl = System.getenv("SPRING_DATASOURCE_URL");
        String r2dbcUrl = System.getenv("SPRING_R2DBC_URL");

        String rawUrl = null;
        if (r2dbcUrl != null && !r2dbcUrl.trim().isEmpty()) {
            rawUrl = r2dbcUrl.trim();
        } else if (dsUrl != null && !dsUrl.trim().isEmpty()) {
            rawUrl = dsUrl.trim();
        } else if (dbUrl != null && !dbUrl.trim().isEmpty()) {
            rawUrl = dbUrl.trim();
        }

        if (rawUrl != null && !rawUrl.isEmpty()) {
            Map<String, Object> map = new HashMap<>();

            try {
                // Nettoie le préfixe de protocole pour analyser l'URI avec java.net.URI
                String uriString = rawUrl.replaceFirst("^(jdbc:|r2dbc:)", "");
                if (uriString.startsWith("postgres://")) {
                    uriString = uriString.replaceFirst("^postgres://", "postgresql://");
                }
                if (!uriString.startsWith("postgresql://")) {
                    return;
                }

                URI uri = URI.create(uriString);
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String path = uri.getPath();

                if (host != null && !host.isEmpty()) {
                    // Les drivers JDBC et R2DBC ne supportent pas 'user:password@' dans le nom d'hôte de l'URL
                    String jdbcCleanUrl = "jdbc:postgresql://" + host + ":" + port + path;
                    String r2dbcCleanUrl = "r2dbc:postgresql://" + host + ":" + port + path;

                    map.put("spring.datasource.url", jdbcCleanUrl);
                    map.put("spring.liquibase.url", jdbcCleanUrl);
                    map.put("spring.r2dbc.url", r2dbcCleanUrl);

                    String userInfo = uri.getUserInfo();
                    if (userInfo != null && userInfo.contains(":")) {
                        String[] parts = userInfo.split(":", 2);
                        map.put("spring.datasource.username", parts[0]);
                        map.put("spring.datasource.password", parts[1]);
                        map.put("spring.r2dbc.username", parts[0]);
                        map.put("spring.r2dbc.password", parts[1]);
                        map.put("spring.liquibase.user", parts[0]);
                        map.put("spring.liquibase.password", parts[1]);
                    }
                }
            } catch (Exception ignored) {
            }

            if (!map.isEmpty()) {
                environment.getPropertySources().addFirst(new MapPropertySource("cloudDatabaseUrlProperties", map));
            }
        }
    }
}
