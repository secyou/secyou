package com.busicomjp.sapp.common.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.security.Decrypt;

@Component
@Configuration
public class DatasourceConfig {
	
	@Value("${jdbc.datasource.url}")
	private String url;
	@Value("${jdbc.datasource.driverClassName}")
	private String driverClassName;
	@Value("${jdbc.datasource.username}")
	private String username;
	@Value("${jdbc.datasource.password}")
	private String password;
	
	@Bean
    public DataSource datasource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(Decrypt.decrypt(username));
        dataSource.setPassword(Decrypt.decrypt(password));
        return dataSource;
    }
}
