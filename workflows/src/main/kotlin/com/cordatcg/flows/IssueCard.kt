package com.cordatcg.flows

import co.paralleluniverse.fibers.Suspendable
import com.cordatcg.domain.Card
import com.cordatcg.states.CardTokenType
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlow
import com.r3.corda.lib.tokens.workflows.flows.issue.addIssueTokens
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class IssueCard(private val card: Card, private val issuerName: String = "O=IssuerA,L=London,C=GB") : FlowLogic<Unit>() {
    @Suspendable
    override fun call(): Unit {

        val issuer: Party = serviceHub.networkMapCache.getPeerByLegalName(CordaX500Name.parse(issuerName))!!
        requireThat {
            "Only the issuer can issue cards".using(issuer == ourIdentity)
        }
        val cardTokenType = CardTokenType(card)
        val cardIssuedTokenType: IssuedTokenType = cardTokenType issuedBy issuer
        val cardToken: NonFungibleToken = NonFungibleToken(
            token = cardIssuedTokenType,
            holder = issuer,
            linearId = UniqueIdentifier()
        )
        val stx = subFlow(IssueTokensFlow(cardToken))
    }

}