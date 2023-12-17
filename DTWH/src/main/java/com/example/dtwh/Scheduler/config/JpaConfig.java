package com.example.dtwh.Scheduler.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

    @Primary
    @Bean(name = "stagingEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean stagingEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("stagingDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.yourcompany.yourproject.model.staging")
                .persistenceUnit("staging")
                .build();
    }

    @Primary
    @Bean(name = "stagingTransactionManager")
    public PlatformTransactionManager stagingTransactionManager(
            @Qualifier("stagingEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
    @Bean(name = "stagingJdbcTemplate")
    public JdbcTemplate stagingJdbcTemplate(@Qualifier("stagingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    @Bean(name = "dwhEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dwhEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dwhDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.yourcompany.yourproject.model.dwh")
                .persistenceUnit("dwh")
                .build();
    }

    @Bean(name = "dwhTransactionManager")
    public PlatformTransactionManager dwhTransactionManager(
            @Qualifier("dwhEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "dwhJdbcTemplate")
    public JdbcTemplate dwhJdbcTemplate(@Qualifier("dwhDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "stagingDataSource")
    public DataSource stagingDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:mysql://localhost:3306/staging_kqxs")
                .username("root")
                .password("")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
    @Bean(name = "dwhDataSource")
    public DataSource dwhDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:mysql://localhost:3306/dwh_kqxs")
                .username("root")
                .password("")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }

}

