package com.sfazzino.portfolio_api.rag

import com.sfazzino.portfolio_api.exception.ApplicationException.Companion.emptyFile
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeAskRequest
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeAskResponse
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeChunkDto
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeIngestResponse
import com.sfazzino.portfolio_api.rag.service.KnowledgeAskService
import com.sfazzino.portfolio_api.rag.service.KnowledgeIngestService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/knowledge")
class KnowledgeController(
    private val askService: KnowledgeAskService,
    private val ingestService: KnowledgeIngestService
) {

    @PostMapping("ingest")
    fun upload(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("source") @NotBlank source: String
    ): ResponseEntity<KnowledgeIngestResponse> {
        if (file.isEmpty) throw emptyFile()

        val ingestionJobId = ingestService.ingestAsync(
            source = source.trim(),
            originalFilename = file.originalFilename,
            contentType = file.contentType,
            bytes = file.bytes
        )

        return ResponseEntity.accepted().body(KnowledgeIngestResponse(jobId = ingestionJobId))
    }

    @GetMapping("ask")
    fun ask(@RequestBody @Valid request: KnowledgeAskRequest): ResponseEntity<KnowledgeAskResponse> {
        val response = askService.ask(request.question.trim())
        return ResponseEntity.ok(response)
    }

    @GetMapping("chunks-raw")
    fun getAllChunksRaw(): ResponseEntity<List<KnowledgeChunkDto>> {
        val chunks = askService.getAllChunksRaw()
        return ResponseEntity.ok(chunks)
    }
}