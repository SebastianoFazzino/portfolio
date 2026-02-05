package com.sfazzino.portfolio_api.rag.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@EnableAsync
@Configuration
class KnowledgeAsyncConfig {

    @Bean(name = ["knowledgeIngestExecutor"])
    fun knowledgeIngestExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 4
        executor.queueCapacity = 50
        executor.setThreadNamePrefix("knowledge-ingest-")
        executor.initialize()
        return executor
    }
}