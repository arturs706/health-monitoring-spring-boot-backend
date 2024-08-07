package uk.co.aipainappserver.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "uk.co.aipainappserver.users.infrastructure_layer.ro",
        entityManagerFactoryRef = "roEntityManagerFactory",
        transactionManagerRef = "roTransactionManager"
)


public class RODataSourceConfig {

    @Bean(name = "roDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.ro")
    public DataSource roDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "roEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean roEntityManagerFactory(
            @Qualifier("roDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("uk.co.aipainappserver.users.domain_layer.entities");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaPropertyMap(jpaProperties());
        return em;
    }

    @Bean(name = "roTransactionManager")
    public PlatformTransactionManager roTransactionManager(
            @Qualifier("roEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.show_sql", "true");
        return props;
    }
}
