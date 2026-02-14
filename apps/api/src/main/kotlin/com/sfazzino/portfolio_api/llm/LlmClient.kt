package com.sfazzino.portfolio_api.llm

import com.sfazzino.portfolio_api.contact.moderation.ModerationDecision

interface LlmClient {
  /**
   * Pre-initializes the LLM client connection to reduce latency on first request.
   */
  fun warmUp()

  /**
   * Converts the given text into a vector embedding for semantic search or similarity comparison.
   * @param text The input text to embed.
   * @return A float array representing the text's embedding vector.
   */
  fun embed(text: String): FloatArray

  /**
   * Analyzes the message content for policy violations or inappropriate content.
   * @param message The user message to moderate.
   * @return A decision indicating whether the message passes moderation.
   */
  fun moderate(message: String): ModerationDecision

  /**
   * Generates a complete response from the LLM based on the given prompt.
   * @param prompt The input prompt to send to the model.
   * @return The generated text response.
   */
  fun generate(prompt: String): String

  /**
   * Stream tokens. If `isCancelled()` becomes true, the implementation MUST
   * close the underlying HTTP connection to stop generation server-side.
   */
  fun generateStream(
    prompt: String,
    isCancelled: () -> Boolean,
    onToken: (String) -> Unit,
  )
}
