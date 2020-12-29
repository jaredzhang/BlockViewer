package com.jaredzhang.blockviewer.repository

import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.api.ChainInfo
import com.jaredzhang.blockviewer.api.ChainService
import com.jaredzhang.blockviewer.utils.CoroutinesDispatcherProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class ChainRepositoryTest {
    private val chainService: ChainService = mock()

    private val dispatcher = TestCoroutineDispatcher()
    private val dispatcherProvider = CoroutinesDispatcherProvider(
        main = dispatcher,
        computation = dispatcher,
        io = dispatcher
    )

    private val chainRepository = ChainRepository(chainService, dispatcherProvider)

    @Test
    fun testGetRecentBlocks() = dispatcher.runBlockingTest {
        // Given
        val expected = listOf(
            Result.Success(BlockInfo(blockNum = 1000)),
            Result.Success(BlockInfo(blockNum = 9999)),
            Result.Success(BlockInfo(blockNum = 9998))
        )

        chainService.stub {
            onBlocking { info() }.thenReturn(ChainInfo(headBlockNum = 1000))
            onBlocking { block(any()) }.thenReturn(expected[0].data, expected[1].data, expected[2].data)
        }

        // When
        val got = chainRepository.getRecentBlocks(3).toList()

        // Then
        kotlin.test.assertEquals(expected, got)
    }

    @Test
    fun testGetBlock() = dispatcher.runBlockingTest {
        // Given
        val expected = Result.Success(
            BlockInfo(
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
        )
        chainService.stub {
            onBlocking { block(any()) }.thenReturn(expected.data)
        }

        // When
        val got = chainRepository.getBlock(43095108).first()

        // Then
        kotlin.test.assertEquals(expected, got)
    }

    @Test
    fun testGetRecentBlocksShouldBeEmptyIfError() = dispatcher.runBlockingTest {
        // Given
        val blocks = listOf(
            Result.Success(BlockInfo(blockNum = 1000)),
            Result.Success(BlockInfo(blockNum = 9999)),
            Result.Success(BlockInfo(blockNum = 9998))
        )
        chainService.stub {
            onBlocking { info() }.thenAnswer {  throw Exception("Error happened") }
            onBlocking { block(any()) }.thenReturn(blocks[0].data, blocks[1].data, blocks[2].data)
        }

        // When
        val got = chainRepository.getRecentBlocks(3).toList()

        // Then
        kotlin.test.assertEquals(emptyList(), got)
    }

    @Test
    fun testGetBlockError() = dispatcher.runBlockingTest {
        // Given
        chainService.stub {
            onBlocking { block(any()) }.doAnswer { throw Throwable("network error") }
        }

        // When
        val got = chainRepository.getBlock(43095108).first()

        // Then
        kotlin.test.assertTrue { got is Result.Error }
    }
}