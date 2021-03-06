package com.sp

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.context.properties.*
import org.springframework.cloud.netflix.eureka.*
import org.springframework.cloud.openfeign.*
import org.springframework.web.reactive.config.*

@SpringBootApplication
@EnableEurekaClient
@ConfigurationPropertiesScan
@EnableWebFlux
@EnableFeignClients
class FrontApplication

fun main(args: Array<String>) {
    runApplication<FrontApplication>(*args)
}
