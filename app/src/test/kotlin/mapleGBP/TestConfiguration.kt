package mapleGBP

import mapleGBP.config.Configuration
import mapleGBP.config.WebConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@ComponentScan(
    basePackages = ["mapleGBP"],
    excludeFilters = [ComponentScan.Filter(WebConfiguration::class, type = FilterType.ASSIGNABLE_TYPE)]
)
class TestConfiguration: Configuration() {

    override fun datasource(): DataSource {
        val dataSource: DriverManagerDataSource = DriverManagerDataSource()
        dataSource.url = "jdbc:mysql://ec2-13-124-101-93.ap-northeast-2.compute.amazonaws.com:3306/test_gbp"
        dataSource.username = "mgbp"
        dataSource.password = "KMS-SERVICE-ADMIN-2021"
        dataSource.connectionProperties = super.additionalJdbcProperties()

        return dataSource
    }
}