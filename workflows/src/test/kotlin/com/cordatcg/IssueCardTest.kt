package com.cordatcg

import com.cordatcg.domain.Card
import com.cordatcg.domain.Element
import net.corda.testing.node.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.cordatcg.states.TemplateState
import java.util.concurrent.Future;
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import com.cordatcg.flows.Initiator
import com.cordatcg.flows.IssueCard
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken
import net.corda.core.concurrent.CordaFuture
import net.corda.core.identity.CordaX500Name
import net.corda.core.node.services.Vault.StateStatus
import net.corda.core.utilities.getOrThrow
import kotlin.test.assertFailsWith


class IssueCardTest {
    private lateinit var network: MockNetwork
    private lateinit var issuer: StartedMockNode
    private lateinit var trader: StartedMockNode
    private lateinit var card: Card
    @Before
    fun setup() {
        network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(
                TestCordapp.findCordapp("com.cordatcg.contracts"),
                TestCordapp.findCordapp("com.cordatcg.flows"),
                TestCordapp.findCordapp("com.r3.corda.lib.tokens.contracts"),
                TestCordapp.findCordapp("com.r3.corda.lib.tokens.workflows")
        )))
        issuer = network.createPartyNode(CordaX500Name.parse("O=IssuerA,L=London,C=GB"))
        trader = network.createPartyNode()
        network.runNetwork()

        card = Card("TestName", "TestDescription", 10, Element.FIRE)
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }
    @Test
    fun `Issue card test`() {
        val flow = IssueCard(card)
        val future: CordaFuture<Unit> = issuer.startFlow(flow)
        network.runNetwork()

        val state = issuer.services.vaultService.queryBy(NonFungibleToken::class.java).states[0].state.data
    }
    @Test
    fun `Issue card unauthorised node test`() {
        val flow = IssueCard(card)
        val future: CordaFuture<Unit> = trader.startFlow(flow)
        network.runNetwork()
        assertFailsWith<IllegalArgumentException>("Only the issuer can issue cards") { future.getOrThrow() }
    }
}