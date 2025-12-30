package com.anubhav.techblog.Techblogging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class ThymeleafWebConfig {

    @Bean
    @Primary
    public SpringTemplateEngine springTemplateEngine(
            SpringResourceTemplateResolver defaultTemplateResolver
    ) {
        SpringTemplateEngine engine = new SpringTemplateEngine();

        // Default resolver: src/main/resources/templates/
        engine.setTemplateResolver(defaultTemplateResolver);

        // REQUIRED for sec:authorize to work
        engine.addDialect(new SpringSecurityDialect());

        return engine;
    }
}
