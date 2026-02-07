package com.sfazzino.portfolio_api.llm.prompt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "llm.prompts")
data class LlmPromptProperties(
    val moderationPath: String,
    val ragPath: String
)