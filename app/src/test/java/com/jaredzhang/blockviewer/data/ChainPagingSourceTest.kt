package com.jaredzhang.blockviewer.data

import androidx.paging.PagingSource
import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.api.ChainInfo
import com.jaredzhang.blockviewer.api.ChainService
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class ChainPagingSourceTest {
    private val chainService: ChainService = mock()

    private val chainPagingSource = ChainPagingSource(chainService)

    @Test
    fun testLoadLatestBlocks() = runBlockingTest {
        // Given
        val expected = listOf(
            BlockInfo(blockNum = 1000L),
            BlockInfo(blockNum = 9999L),
            BlockInfo(blockNum = 9998L)
        )

        chainService.stub {
            onBlocking { info() }.thenReturn(ChainInfo(headBlockNum = 1000))
            onBlocking { block(any()) }.thenReturn(expected[0], expected[1], expected[2])
        }

        // When
        val got = chainPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        // Then
        kotlin.test.assertEquals(expected,
            (got as PagingSource.LoadResult.Page<Long, BlockInfo>).data)
        kotlin.test.assertEquals(9997L, got.nextKey)
    }

    @Test
    fun testAppendBlocks() = runBlockingTest {
        // Given
        val expected = listOf(
            BlockInfo(blockNum = 1000L),
            BlockInfo(blockNum = 9999L),
            BlockInfo(blockNum = 9998L)
        )

        chainService.stub {
            onBlocking { info() }.thenReturn(ChainInfo(headBlockNum = 1000))
            onBlocking { block(any()) }.thenReturn(expected[0], expected[1], expected[2])
        }

        // When
        val got = chainPagingSource.load(
            PagingSource.LoadParams.Append(
                key = 1000,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        // Then
        kotlin.test.assertEquals(expected,
            (got as PagingSource.LoadResult.Page<Long, BlockInfo>).data)
        kotlin.test.assertEquals(9997L, got.nextKey)
    }

    @Test
    fun testGetBlocksError() = runBlockingTest {
        // Given
        chainService.stub {
            onBlocking { info() }.thenAnswer { throw Exception("Error happened") }
        }

        // When
        val got = chainPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        // Then
        kotlin.test.assertTrue { got is PagingSource.LoadResult.Error }
    }

    @Test
    fun testErrorIfGetBlocksError() = runBlockingTest {
        // Given
        chainService.stub {
            onBlocking { block(any()) }.doAnswer { throw Throwable("network error") }
        }

        // When
        val got = chainPagingSource.load(
            PagingSource.LoadParams.Append(
                key = 1000L,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        // Then
        kotlin.test.assertTrue { got is PagingSource.LoadResult.Error }
    }
}