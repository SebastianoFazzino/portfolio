package com.sfazzino.portfolio_api.contact.moderation

data class ModerationDecision(
  val verdict: ModerationVerdict,
  val reason: String? = null
) {
  val allowed: Boolean get() = verdict == ModerationVerdict.ALLOW

  companion object {
    fun allow() = ModerationDecision(ModerationVerdict.ALLOW)
    fun block(reason: String? = null) = ModerationDecision(ModerationVerdict.BLOCK, reason)
  }
}
