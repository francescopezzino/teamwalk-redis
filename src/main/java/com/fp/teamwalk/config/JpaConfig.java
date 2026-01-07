package com.fp.teamwalk.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement // Ensures @Transactional is processed
@EntityScan(basePackages = "com.fp.teamwalk.domain") // Locates your Employee, Team, StepCounter entities
@EnableJpaRepositories(basePackages = "com.fp.teamwalk.repos") // Locates your Repositories
public class JpaConfig {
    // Customization for persistence providers or auditing can be added here
}
