package com.work.taskCatalog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
class TaskCatalogApplication

fun main(args: Array<String>) {
	runApplication<TaskCatalogApplication>(*args)
}
