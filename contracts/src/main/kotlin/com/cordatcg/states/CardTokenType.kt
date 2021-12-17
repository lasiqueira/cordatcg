package com.cordatcg.states

import com.cordatcg.domain.Card
import com.r3.corda.lib.tokens.contracts.types.TokenType


data class CardTokenType (val card:Card,
                          override val tokenIdentifier: String = card.name,
                          override val fractionDigits: Int = 1

) : TokenType(tokenIdentifier, fractionDigits)
