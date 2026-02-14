package com.sfazzino.portfolio_api.knowledge

import com.sfazzino.portfolio_api.exception.ApplicationException.Companion.emptyFile
import com.sfazzino.portfolio_api.knowledge.dtos.KnowledgeAskRequest
import com.sfazzino.portfolio_api.knowledge.dtos.KnowledgeAskResponse
import com.sfazzino.portfolio_api.knowledge.dtos.KnowledgeChunkDto
import com.sfazzino.portfolio_api.knowledge.dtos.KnowledgeIngestResponse
import com.sfazzino.portfolio_api.knowledge.service.KnowledgeAskService
import com.sfazzino.portfolio_api.knowledge.service.KnowledgeIngestService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

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

        val ingestionJobId = ingestService.ingest(
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

    @GetMapping("ask/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun askStream(@RequestParam question: String): SseEmitter {
        val emitter = SseEmitter(0L)
        askService.askStream(question.trim(), emitter)
        return emitter
    }

    @GetMapping("chunks-raw")
    fun getAllChunksRaw(): ResponseEntity<List<KnowledgeChunkDto>> {
        val chunks = askService.getAllChunksRaw()
        return ResponseEntity.ok(chunks)
    }
}