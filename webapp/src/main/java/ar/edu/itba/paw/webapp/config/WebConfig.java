package ar.edu.itba.paw.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.sql.DataSource;

@ComponentScan({
        "ar.edu.itba.paw.webapp.controller",
        "ar.edu.itba.paw.service",
        "ar.edu.itba.paw.persistence"
})
@EnableWebMvc
@Configuration
public class WebConfig {

    @Bean
    public ViewResolver viewResolver() {
        final InternalResourceViewResolver resolver = new InternalResourceViewResolver();

        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/");
        resolver.setSuffix(".jsp");

        return resolver;
    }

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();

        ds.setDriverClass(org.postgresql.Driver.class);
        ds.setUrl("jdbc:postgresql://10.16.1.110/paw-2022a-09");
        ds.setUsername("paw-2022a-09");
        ds.setPassword("y87lZUffp");

        return ds;
    }

}
