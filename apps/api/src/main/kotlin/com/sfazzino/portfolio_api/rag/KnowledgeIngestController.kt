package com.sfazzino.portfolio_api.rag

import com.sfazzino.portfolio_api.exception.ApplicationException.Companion.emptyFile
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeIngestResponse
import com.sfazzino.portfolio_api.rag.service.KnowledgeIngestService
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/knowledge-ingest")
class KnowledgeIngestController(
    private val service: KnowledgeIngestService
) {

    @PostMapping
    fun upload(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("source") @NotBlank source: String
    ): ResponseEntity<KnowledgeIngestResponse> {
        if (file.isEmpty) throw emptyFile()

        val ingestionJobId = service.ingestAsync(
            source = source.trim(),
            originalFilename = file.originalFilename,
            contentType = file.contentType,
            bytes = file.bytes
        )

        return ResponseEntity.accepted().body(KnowledgeIngestResponse(jobId = ingestionJobId))
    }
}