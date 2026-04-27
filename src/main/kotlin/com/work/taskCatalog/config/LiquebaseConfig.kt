package com.work.taskCatalog.config
import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource
@Configuration
class LiquibaseConfig {
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
            .url("jdbc:postgresql://localhost:5432/taskdb")
            .username("liquibase_user")
            .password("liquibase_pass")
            .driverClassName("org.postgresql.Driver")
            .build()
    }
}