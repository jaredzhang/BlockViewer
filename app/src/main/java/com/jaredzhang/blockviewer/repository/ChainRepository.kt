package com.jaredzhang.blockviewer.repository

import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.api.BlockRequest
import com.jaredzhang.blockviewer.api.ChainService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ChainRepository constructor(private val chainService: ChainService, private val coroutineDispatcher: CoroutineDispatcher) {

    @FlowPreview
    fun getRecentBlocks(lastCount: Int): Flow<Result<BlockInfo>> {
        return getLatestBlockNum(lastCount)
            .flatMapConcat { blockNum ->
                if (blockNum <= 0) emptyFlow()
                else getBlock(blockNum)
            }
            .flowOn(coroutineDispatcher)
    }

    fun getBlock(blockNum: Int): Flow<Result<BlockInfo>> {
        return flow <Result<BlockInfo>> {
                emit(Result.Success(chainService.block(BlockRequest(blockNum))))
            }
            .flowOn(coroutineDispatcher)
            .catch { error ->
                emit(Result.Error("could not get block info", error))
            }
    }

    private fun getLatestBlockNum(lastCount: Int): Flow<Int> {
        return flow {
                val latestBlockNum = chainService.info().headBlockNum ?: 0
                (latestBlockNum downTo Math.max(1, latestBlockNum - lastCount + 1)).map { blockNum ->
                    emit(blockNum)
                }
            }
            .flowOn(coroutineDispatcher)
            .catch {
                emit(0)
            }
    }
}

sealed class Result<out T : Any> {
    data class Success<out T: Any>(val data: T): Result<T>()
    data class Error(val message: String, val error: Throwable): Result<Nothing>()
}