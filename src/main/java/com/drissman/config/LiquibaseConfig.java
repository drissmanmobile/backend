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
        
        String cleanUrl = url;
        String cleanUsername = username;
        String cleanPassword = password;
        
        if (cleanUrl != null && (cleanUrl.contains("@") || cleanUrl.startsWith("postgres://") || cleanUrl.startsWith("postgresql://"))) {
            try {
                String uriString = cleanUrl.replace("jdbc:", "");
                if (uriString.startsWith("postgres://")) {
                    uriString = uriString.replace("postgres://", "postgresql://");
                }
                java.net.URI uri = new java.net.URI(uriString);
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String path = uri.getPath();
                cleanUrl = "jdbc:postgresql://" + host + ":" + port + (path != null && !path.isEmpty() ? path : "/drissman");
                
                if (uri.getUserInfo() != null) {
                    String[] userParts = uri.getUserInfo().split(":");
                    if (userParts.length > 0 && userParts[0] != null && !userParts[0].isEmpty()) {
                        cleanUsername = userParts[0];
                    }
                    if (userParts.length > 1 && userParts[1] != null && !userParts[1].isEmpty()) {
                        cleanPassword = userParts[1];
                    }
                }
            } catch (Exception e) {
                if (!cleanUrl.startsWith("jdbc:")) {
                    cleanUrl = "jdbc:" + cleanUrl;
                }
            }
        } else if (cleanUrl != null && !cleanUrl.startsWith("jdbc:")) {
            cleanUrl = "jdbc:" + cleanUrl;
        }
        
        dataSource.setUrl(cleanUrl);
        dataSource.setUsername(cleanUsername);
        dataSource.setPassword(cleanPassword);
        return dataSource;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
        return liquibase;
    }
}
