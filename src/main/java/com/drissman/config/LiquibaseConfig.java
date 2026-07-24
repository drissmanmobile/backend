package com.drissman.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);

        String[] parsed = parseDatabaseUrl(url, username, password);
        dataSource.setUrl(parsed[0]);
        dataSource.setUsername(parsed[1]);
        dataSource.setPassword(parsed[2]);

        return dataSource;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
        return liquibase;
    }

    private static String[] parseDatabaseUrl(String rawUrl, String defaultUser, String defaultPass) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            return new String[] { "jdbc:postgresql://127.0.0.1:5433/drissman", defaultUser, defaultPass };
        }

        String work = rawUrl.trim();
        if (work.startsWith("jdbc:")) {
            work = work.substring(5);
        }
        if (work.startsWith("postgresql://")) {
            work = work.substring(13);
        } else if (work.startsWith("postgres://")) {
            work = work.substring(11);
        }

        String user = defaultUser;
        String pass = defaultPass;

        if (work.contains("@")) {
            int atIndex = work.lastIndexOf("@");
            String userPassPart = work.substring(0, atIndex);
            work = work.substring(atIndex + 1);

            if (userPassPart.contains(":")) {
                int colonIndex = userPassPart.indexOf(":");
                user = userPassPart.substring(0, colonIndex);
                pass = userPassPart.substring(colonIndex + 1);
            } else {
                user = userPassPart;
            }
        }

        String hostPortDb = work;
        String host = "127.0.0.1";
        String port = "5432";
        String db = "drissman";

        if (hostPortDb.contains("/")) {
            int slashIndex = hostPortDb.indexOf("/");
            String hostPortPart = hostPortDb.substring(0, slashIndex);
            String dbPart = hostPortDb.substring(slashIndex + 1);

            if (dbPart.contains("?")) {
                dbPart = dbPart.substring(0, dbPart.indexOf("?"));
            }
            if (!dbPart.trim().isEmpty()) {
                db = dbPart;
            }
            hostPortDb = hostPortPart;
        }

        if (hostPortDb.contains(":")) {
            int colonIndex = hostPortDb.indexOf(":");
            host = hostPortDb.substring(0, colonIndex);
            port = hostPortDb.substring(colonIndex + 1);
        } else if (!hostPortDb.trim().isEmpty()) {
            host = hostPortDb;
        }

        String cleanJdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        return new String[] { cleanJdbcUrl, user, pass };
    }
}
