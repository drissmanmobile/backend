package com.drissman;

import org.springframework.boot.CommandLineRunner;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    private final DatabaseClient databaseClient;

    public TestRunner(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== RUNNING TEST INSERT ===");
        databaseClient.sql("INSERT INTO users (email, password, first_name, last_name, role, is_active) VALUES ($1, $2, $3, $4, $5, $6)")
                .bind(0, "test10@drissman.cm")
                .bind(1, "pass")
                .bind(2, "first")
                .bind(3, "last")
                .bind(4, "STUDENT")
                .bind(5, true)
                .fetch().rowsUpdated()
                .doOnError(e -> {
                    System.err.println("=== INSERT FAILED ===");
                    e.printStackTrace();
                })
                .subscribe(rows -> System.out.println("=== INSERT SUCCESS: " + rows + " ==="));
    }
}
