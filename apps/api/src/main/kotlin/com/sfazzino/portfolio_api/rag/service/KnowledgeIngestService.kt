package com.sfazzino.portfolio_api.rag.service

import com.sfazzino.portfolio_api.exception.ApplicationException.Companion.invalidFile
import org.apache.tika.Tika
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.util.*

@Service
class KnowledgeIngestService(
    private val knowledgeIndexService: KnowledgeIndexService
) {
    fun ingestAsync(
        source: String,
        originalFilename: String?,
        contentType: String?,
        bytes: ByteArray
    ): UUID {
        val jobId = UUID.randomUUID()
        ingestInBackground(
            jobId = jobId,
            source = source,
            originalFilename = originalFilename,
            contentType = contentType,
            bytes = bytes
        )
        return jobId
    }

    @Async("knowledgeIngestExecutor")
    fun ingestInBackground(
        jobId: UUID,
        source: String,
        originalFilename: String?,
        contentType: String?,
        bytes: ByteArray
    ) {
        try {
            val extractedText = extractText(
                originalFilename = originalFilename,
                contentType = contentType,
                bytes = bytes
            )

            knowledgeIndexService.ingestText(
                source = source,
                text = extractedText
            )

            log.info("Knowledge ingest completed jobId=$jobId source=$source")
        } catch (ex: Exception) {
            log.warn( "Knowledge ingest failed jobId=$jobId source=$source", ex)
        }
    }

    fun extractText(originalFilename: String?, contentType: String?, bytes: ByteArray): String {
        val extractedText = ByteArrayInputStream(bytes)
            .use { inputStream -> tika.parseToString(inputStream) }.trim()

        if (extractedText.isEmpty()) {
            throw invalidFile("No text extracted from document filename=$originalFilename contentType=$contentType")
        }
        return extractedText
    }

    companion object {
        private val tika = Tika()
        private val log = LoggerFactory.getLogger(KnowledgeIngestService::class.java)
    }
}
