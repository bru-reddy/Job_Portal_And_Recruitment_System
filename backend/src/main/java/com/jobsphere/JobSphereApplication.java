package com.jobsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class JobSphereApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobSphereApplication.class, args);
    }

    @Bean
    CommandLineRunner ensureJobSchema(JdbcTemplate jdbc) {
        return args -> {
            jdbc.execute("ALTER TABLE jobs ADD COLUMN IF NOT EXISTS source_name varchar(500)");
            jdbc.execute("ALTER TABLE jobs ADD COLUMN IF NOT EXISTS source_url varchar(2000)");
            jdbc.execute("ALTER TABLE jobs ADD COLUMN IF NOT EXISTS eligibility_criteria varchar(1000)");
            jdbc.execute("ALTER TABLE jobs ALTER COLUMN recruiter_id DROP NOT NULL");
        };
    }
}
