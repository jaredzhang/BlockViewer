package com.jaredzhang.blockviewer.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.api.BlockRequest
import com.jaredzhang.blockviewer.api.ChainService
import com.jaredzhang.blockviewer.utils.CoroutinesDispatcherProvider
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@FlowPreview
class ChainRepository @Inject constructor(
    private val chainService: ChainService,
    private val chainPagingSource: ChainPagingSource,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider
) {

    companion object {
        const val PAGE_SIZE = 20
    }

    fun getBlocks(): Flow<PagingData<BlockInfo>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { chainPagingSource }
        ).flow
    }

    fun getBlock(blockNum: Long): Flow<Result<BlockInfo>> {
        return flow <Result<BlockInfo>> {
                emit(Result.Success(
                    chainService.block(BlockRequest(blockNum))))
            }
            .flowOn(coroutinesDispatcherProvider.io)
            .catch { error ->
                emit(Result.Error(
                    "could not get block info", error))
            }
    }

}

sealed class Result<out T : Any> {
    data class Success<out T: Any>(val data: T): Result<T>()
    data class Error(val message: String, val error: Throwable): Result<Nothing>()
}