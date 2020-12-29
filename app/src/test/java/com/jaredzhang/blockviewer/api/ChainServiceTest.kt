package com.jaredzhang.blockviewer.api

import com.jaredzhang.blockviewer.rule.CoroutineRule
import com.jaredzhang.blockviewer.rule.MockWebServerTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import okhttp3.mockwebserver.MockResponse
import okio.buffer
import okio.source
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@ExperimentalCoroutinesApi
class ChainServiceTest {

    private lateinit var chainService: ChainService

    @get:Rule
    val mockWebServerRule = MockWebServerTestRule()

    @get:Rule
    val coroutineRule = CoroutineRule(TestCoroutineDispatcher())

    companion object {
        fun create(fqdn: String): ChainService {
            return Retrofit.Builder()
                .baseUrl(fqdn)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChainService::class.java)
        }
    }

    @Before
    fun setUp() {
        chainService = create(mockWebServerRule.baseUrl)
    }

    @Test
    fun testInfoSuccess() = runBlocking {
        // Given
        val expected = ChainInfo(
            serverVersion = "0d87dff8",
            chainId = "cf057bbfb72640471fd910bcb67639c22df9f92470936cddc1ade0e2f2e7dc4f",
            headBlockNum = 43095108,
            lastIrreversibleBlockNum = 43094780,
            lastIrreversibleBlockId = "029192fc4d82acb9d780a538599d869984025ebb7c4801970749cff52468b505",
            headBlockId = "02919444d10830cc61e6f911fdbcda64232b843baaa0aaaaf49f41324cc037ca",
            headBlockTime = "2020-12-29T07:58:55.500",
            headBlockProducer = "inite",
            virtualBlockCpuLimit = 200000000,
            virtualBlockNetLimit = 1048576000,
            blockCpuLimit = 199900,
            blockNetLimit = 1048576,
            serverVersionString = "v2.0.7",
            forkDbHeadBlockNum = 43095108,
            forkDbHeadBlockId = "02919444d10830cc61e6f911fdbcda64232b843baaa0aaaaf49f41324cc037ca",
            serverFullVersionString = "v2.0.7-0d87dff8bee56179aa01472dd00a089b2aa7b9fa"
        )
        enqueueMockResponse("info.json")

        // When
        val got = chainService.info()

        // Then
        kotlin.test.assertEquals(expected, got)
    }

    @Test
    fun testListBlockSucess()= runBlocking {
        // Given
        val expected = BlockInfo(
            timestamp = "2020-12-29T07:58:55.500",
            producer = "inite",
            confirmed = 0,
            previous = "02919443fc2328510ca4280158d182f5f60f2d95197af5f4d74b4157ce0c8cb1",
            transactionMroot = "0000000000000000000000000000000000000000000000000000000000000000",
            actionMroot = "4462430c1f000fafc685afa9ec8ef11c331778bf325dfb8e7bbcb6b4d4e3e0ed",
            scheduleVersion = 1,
            newProducers = null,
            producerSignature = "SIG_K1_KhyamznV5432UMf8P7D9FrNbH1NfV6hHbp7HN5tUn6QPxJCdcqFZyw6nvzk8fVawBbyaQ5mWMCy8DoUuRRiMS66m6gzyKH",
            transactions = emptyList(),
            id = "02919444d10830cc61e6f911fdbcda64232b843baaa0aaaaf49f41324cc037ca",
            blockNum = 43095108,
            refBlockPrefix = 301590113
        )
        enqueueMockResponse("block.json")

        // When
        val got = chainService.block(BlockRequest(43095108))

        // Then
        kotlin.test.assertEquals(expected, got)
    }

    private fun enqueueMockResponse(path: String) {
        val source = File("src/test/resources/$path").source()
        mockWebServerRule.respondWith(MockResponse()
            .setBody(source.buffer().readUtf8()))
        source.close()
    }
}