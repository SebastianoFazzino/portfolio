package com.sfazzino.portfolio_api.common

import com.sfazzino.portfolio_api.contact.moderation.llm.LlmClient
import org.springframework.boot.info.BuildProperties
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class AppController(
    private val build: BuildProperties,
    private val llmClient: LlmClient
) {

    @GetMapping(value = ["healthz"])
    fun healthz(): ResponseEntity<HealthStatus> {
        return ResponseEntity.ok(HealthStatus(build.version ?: "N/A"))
    }

    @PostMapping("ping")
    fun ping(): ResponseEntity<Void> {
        llmClient.warmUp()
        return ResponseEntity.noContent().build()
    }
}

data class HealthStatus(
    val version: String,
    val status: String = "OK"
)