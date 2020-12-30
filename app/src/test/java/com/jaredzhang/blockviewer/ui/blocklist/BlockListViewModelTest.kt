package com.jaredzhang.blockviewer.ui.blocklist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.data.ChainRepository
import com.jaredzhang.blockviewer.data.Result
import com.jaredzhang.blockviewer.rule.CoroutineRule
import com.jaredzhang.blockviewer.utils.captureValues
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class BlockListViewModelTest {
    @get:Rule
    var instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule(TestCoroutineDispatcher())

    private val repository: ChainRepository = mock()

    private val viewModel = BlockListViewModel(repository)

    @Test
    fun testErrorStateIfEmptyResult() {
        // Given
        repository.stub {
            onBlocking { getRecentBlocks(20) }.thenReturn(emptyFlow())
        }

        // When
        viewModel.viewState.captureValues {
            viewModel.fetchBlocks()
            // Then

            kotlin.test.assertTrue {
                values[0] is ViewState.Loading
            }

            kotlin.test.assertTrue {
                values[1] is ViewState.Error
            }
        }
    }

    @Test
    fun testErrorStateIfAllErrorResult() {
        // Given
        repository.stub {
            onBlocking { getRecentBlocks(20) }.thenReturn(listOf(
                Result.Error("error", Throwable("error"))).asFlow())
        }

        // When
        viewModel.viewState.captureValues {
            viewModel.fetchBlocks()
            // Then

            kotlin.test.assertTrue {
                values[0] is ViewState.Loading
            }

            kotlin.test.assertTrue {
                values[1] is ViewState.Error
            }
        }
    }

    @Test
    fun testLoadedStateIfSuccess() {
        // Given
        val data = listOf(BlockInfo(blockNum = 111))
        repository.stub {
            onBlocking { getRecentBlocks(20) }.thenReturn(listOf(
                Result.Success(
                data[0])).asFlow())
        }

        // When
        viewModel.viewState.captureValues {
            viewModel.fetchBlocks()
            // Then

            kotlin.test.assertTrue {
                values[0] is ViewState.Loading
            }

            kotlin.test.assertEquals(ViewState.DataLoaded(data), values[1])
        }
    }
}