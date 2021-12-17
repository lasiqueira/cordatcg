package com.cordatcg.domain

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class Card(val name:String,
                val description: String,
                val power: Int,
                val element: Element)
