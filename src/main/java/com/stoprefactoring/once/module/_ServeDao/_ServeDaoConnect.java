package com.stoprefactoring.once.module._ServeDao;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
//@EnableTransactionManagement
public class _ServeDaoConnect {
    @Bean(name = "Module_ServeDao.ConnectConfig")
    @ConfigurationProperties(value = "module.sr-servedao.datasource")
    public DataSourceProperties _ServeDaoConnectConfig() {
        return new DataSourceProperties();
    }

    @Bean(name = "Module_ServeDao.ConnectDataSource")
    @ConfigurationProperties(value = "module.sr-servedao.datasource.hikari")
    public HikariDataSource _ServeDaoConnectDataSource(@Qualifier("Module_ServeDao.ConnectConfig") DataSourceProperties dataSourceProperties) {
        return (HikariDataSource)dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "Module_ServeDao.ConnectHandler")
    public JdbcTemplate _ServeDaoConnectHandler(@Qualifier("Module_ServeDao.ConnectDataSource") HikariDataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "Module_ServeDao.Transaction")
    public PlatformTransactionManager _ServeDaoTransaction(@Qualifier("Module_ServeDao.ConnectDataSource") HikariDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
