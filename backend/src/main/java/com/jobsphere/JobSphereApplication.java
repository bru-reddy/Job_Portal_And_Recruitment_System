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
            // Keep the jobs table compatible with older Render/PostgreSQL databases.
            jdbc.execute("ALTER TABLE jobs ADD COLUMN IF NOT EXISTS source_name TEXT");
            jdbc.execute("ALTER TABLE jobs ADD COLUMN IF NOT EXISTS source_url TEXT");
            jdbc.execute("ALTER TABLE jobs ADD COLUMN IF NOT EXISTS eligibility_criteria TEXT");
            jdbc.execute("ALTER TABLE jobs ADD COLUMN IF NOT EXISTS description TEXT");
            jdbc.execute("ALTER TABLE jobs ADD COLUMN IF NOT EXISTS skills TEXT");
            jdbc.execute("ALTER TABLE jobs ALTER COLUMN source_name TYPE TEXT");
            jdbc.execute("ALTER TABLE jobs ALTER COLUMN source_url TYPE TEXT");
            jdbc.execute("ALTER TABLE jobs ALTER COLUMN eligibility_criteria TYPE TEXT");
            jdbc.execute("ALTER TABLE jobs ALTER COLUMN description TYPE TEXT");
            jdbc.execute("ALTER TABLE jobs ALTER COLUMN skills TYPE TEXT");
            jdbc.execute("ALTER TABLE jobs ALTER COLUMN recruiter_id DROP NOT NULL");
        };
    }
}
