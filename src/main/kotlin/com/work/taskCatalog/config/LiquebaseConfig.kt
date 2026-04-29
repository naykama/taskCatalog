package com.work.taskCatalog.config
import liquibase.integration.spring.SpringLiquibase
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource
@Configuration
class LiquibaseConfig {

    @Value("\${LIQUIBASE_URL:jdbc:postgresql://localhost:5432/taskdb}")
    private lateinit var url: String
    @Value("\${LIQUIBASE_USERNAME:liquibase_user}")
    private lateinit var username: String
    @Value("\${LIQUIBASE_PASSWORD:liquibase_pass}")
    private lateinit var password: String

    @Bean
    fun liquibase(): SpringLiquibase {
        val liquibase = SpringLiquibase()
        liquibase.setDataSource(liquibaseDataSource())
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml")
        liquibase.setContexts("development")
        liquibase.setShouldRun(true)
        liquibase.setDefaultSchema("public")
        liquibase.setLiquibaseSchema("liquibase")
        return liquibase
    }
    @Bean
    fun liquibaseDataSource(): DataSource {
        return DataSourceBuilder.create()
            .url(url)
            .username(username)
            .password(password)
            .driverClassName("org.postgresql.Driver")
            .build()
    }
}