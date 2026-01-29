package com.sfazzino.portfolio_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class PortfolioApiApplication

fun main(args: Array<String>) {
	runApplication<PortfolioApiApplication>(*args)
}
