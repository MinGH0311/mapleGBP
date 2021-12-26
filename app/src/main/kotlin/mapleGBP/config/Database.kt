package mapleGBP.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(basePackages = arrayOf("mapleGBP.dao"))
@EnableTransactionManagement
@ComponentScan(basePackages = arrayOf("mapleGBP.dao"))
open class Database {

    @Bean
    open fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val em: LocalContainerEntityManagerFactoryBean = LocalContainerEntityManagerFactoryBean();
        em.dataSource = datasource()
        em.setPackagesToScan("mapleGBP.model")

        val vendorAdapter: JpaVendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter
        em.setJpaProperties(additionalProperties())

        return em
    }

    @Bean
    open fun datasource(): DataSource {
        val dataSource: DriverManagerDataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver")
        dataSource.url = "jdbc:mysql://ec2-13-124-101-93.ap-northeast-2.compute.amazonaws.com:3306/gbp"
        dataSource.username = "mgbp"
        dataSource.password = "KMS-SERVICE-ADMIN-2021"
        dataSource.connectionProperties = additionalJdbcProperties()

        return dataSource
    }

    @Bean
    open fun transactionManager(): PlatformTransactionManager {
        val transactionManager: JpaTransactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory().`object`

        return transactionManager
    }

    @Bean
    open fun exceptionTranslation(): PersistenceExceptionTranslationPostProcessor {
        return PersistenceExceptionTranslationPostProcessor()
    }

    private fun additionalProperties(): Properties {
        val properties: Properties = Properties()
        properties.setProperty("hibernate.hbm2ddl.auto", "update")
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
        properties.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy")
        properties.setProperty("hibernate.show_sql", "true")
        return properties
    }

    protected fun additionalJdbcProperties(): Properties {
        val jdbcProperties: Properties = Properties()
        jdbcProperties.setProperty("characterEncoding", "UTF-8")

        return jdbcProperties
    }
}