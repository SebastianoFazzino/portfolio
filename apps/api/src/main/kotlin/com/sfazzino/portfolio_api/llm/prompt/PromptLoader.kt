package com.sfazzino.portfolio_api.llm.prompt

import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component
class PromptLoader(
    private val resourceLoader: ResourceLoader
) {
    fun load(path: String): String {
        return resourceLoader.getResource(path)
            .inputStream
            .bufferedReader()
            .use { it.readText() }
            .trim()
    }
}
