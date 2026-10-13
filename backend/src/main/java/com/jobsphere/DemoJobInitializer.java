package com.jobsphere;

import org.springframework.context.annotation.Configuration;

/**
 * Intentionally does not reset production data at application startup.
 * Jobs and applications are persistent user data and must never be deleted
 * automatically during a deployment or restart.
 */
@Configuration
public class DemoJobInitializer {
    // No startup data deletion or reset logic.
}
