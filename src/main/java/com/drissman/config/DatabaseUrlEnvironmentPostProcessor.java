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
 * aux formats JDBC (jdbc:postgresql://...) et R2DBC (r2dbc:postgresql://...).
 * 
 * Gère aussi les URLs Railway natives (postgresql://...) qui arrivent
 * via les références de variables de service.
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

        if (rawUrl != null && !rawUrl.isEmpty() && !rawUrl.startsWith("$")) {
            Map<String, Object> map = new HashMap<>();

            String jdbcUrl;
            String r2dbcCleanUrl;

            if (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://")) {
                jdbcUrl = rawUrl.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
                r2dbcCleanUrl = rawUrl.replaceFirst("^postgres(ql)?://", "r2dbc:postgresql://");
            } else if (rawUrl.startsWith("jdbc:postgresql://")) {
                jdbcUrl = rawUrl;
                r2dbcCleanUrl = rawUrl.replace("jdbc:", "r2dbc:");
            } else if (rawUrl.startsWith("r2dbc:postgresql://")) {
                r2dbcCleanUrl = rawUrl;
                jdbcUrl = rawUrl.replace("r2dbc:", "jdbc:");
            } else {
                return;
            }

            map.put("spring.datasource.url", jdbcUrl);
            map.put("spring.liquibase.url", jdbcUrl);
            map.put("spring.r2dbc.url", r2dbcCleanUrl);

            // Extrait l'utilisateur et le mot de passe s'ils sont intégrés dans l'URL
            try {
                String uriString = rawUrl.replaceFirst("^(jdbc:|r2dbc:)", "");
                if (uriString.startsWith("postgres://")) {
                    uriString = uriString.replaceFirst("^postgres://", "postgresql://");
                }
                URI uri = URI.create(uriString);
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
            } catch (Exception ignored) {
            }

            environment.getPropertySources().addFirst(new MapPropertySource("cloudDatabaseUrlProperties", map));
        }
    }
}

