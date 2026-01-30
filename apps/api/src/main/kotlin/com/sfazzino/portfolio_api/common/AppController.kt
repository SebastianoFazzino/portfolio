package com.sfazzino.portfolio_api.common

import org.springframework.boot.info.BuildProperties
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class AppController(
    private val build: BuildProperties
) {

    @GetMapping(value = ["healthz"])
    fun healthz(): ResponseEntity<HealthStatus> {
        return ResponseEntity.ok(HealthStatus(build.version ?: "N/A"))
    }
}

data class HealthStatus(
    val version: String,
    val status: String = "OK"
)