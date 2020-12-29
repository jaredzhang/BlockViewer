package com.jaredzhang.blockviewer.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.GsonBuilder
import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.repository.ChainRepository
import com.jaredzhang.blockviewer.repository.Result
import com.jaredzhang.blockviewer.rule.CoroutineRule
import com.jaredzhang.blockviewer.utils.captureValues
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class BlockDetailViewModelTest {
    @get:Rule
    var instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule(TestCoroutineDispatcher())

    private val repostiory: ChainRepository = mock()

    private val viewModel = BlockDetailViewModel(repostiory)

    private var gson = GsonBuilder().setPrettyPrinting().create()

    @Test
    fun testErrorStateIfAllErrorResult() {
        // Given
        repostiory.stub {
            onBlocking { getBlock(1000) }.thenReturn(listOf(Result.Error("error", Throwable("error"))).asFlow())
        }

        // When
        viewModel.viewState.captureValues {
            viewModel.fetchBlockDetail(1000)
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
        val data = BlockInfo(blockNum = 1000)
        repostiory.stub {
            onBlocking { getBlock(1000) }.thenReturn(listOf(Result.Success(
                data)).asFlow())
        }

        // When
        viewModel.viewState.captureValues {
            viewModel.fetchBlockDetail(1000)
            // Then

            kotlin.test.assertTrue {
                values[0] is ViewState.Loading
            }

            kotlin.test.assertEquals(ViewState.DataLoaded(data, gson.toJson(data)), values[1])
        }
    }
}