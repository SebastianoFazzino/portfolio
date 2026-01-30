package com.sfazzino.portfolio_api.contact.moderation

interface ModerationService {
  fun check(message: String): ModerationDecision
}
