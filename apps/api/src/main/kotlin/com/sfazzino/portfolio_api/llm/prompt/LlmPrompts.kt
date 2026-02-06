package com.sfazzino.portfolio_api.llm.prompt

import org.springframework.stereotype.Component

@Component
class LlmPrompts(
    loader: PromptLoader,
    props: LlmPromptProperties
) {
    val moderation: String = loader.load(props.moderationPath)
    val rag: String = loader.load(props.ragPath)
}
