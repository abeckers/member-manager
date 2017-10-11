/*
 * (c) Copyright 2014 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 12.12.2014 by Andreas Beckers
 */
package de.beckers.members;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import de.beckers.members.filters.HttpsFilter;

/**
 * In production add the https filter.
 * 
 * @author Andreas Beckers
 */
@Configuration
@Profile("production")
public class ProductionConfiguration {
    /**
     * Filter für Auhentifizierung.
     * 
     * @return Filter.
     */
    @Bean
    public Filter httpsFilter() {
        return new HttpsFilter();
    }
}
