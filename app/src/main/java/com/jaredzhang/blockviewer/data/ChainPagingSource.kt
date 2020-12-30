package com.jaredzhang.blockviewer.data

import androidx.paging.PagingSource
import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.api.BlockRequest
import com.jaredzhang.blockviewer.api.ChainService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import javax.inject.Inject
import kotlin.math.max

@FlowPreview
class ChainPagingSource @Inject constructor(
    private val chainService: ChainService
) : PagingSource<Long, BlockInfo>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, BlockInfo> {
        var error: LoadResult<Long, BlockInfo>? = null
        val blocks = getBlocks(params.key, params.loadSize)
            .catch {
                error = LoadResult.Error(it)
            }
            .toList()
        return error ?: LoadResult.Page(blocks, null, (blocks.lastOrNull()?.blockNum ?: 0)- 1)
    }

    private fun getBlocks(lastBlockNum: Long?, pageSize: Int): Flow<BlockInfo> {
        return getBlockNumbers(pageSize) { lastBlockNum ?: chainService.info().headBlockNum ?: 0 }
            .flatMapConcat { getBlock(it) }
    }

    private fun getBlock(blockNum: Long): Flow<BlockInfo> {
        return flow {
                emit(chainService.block(BlockRequest(blockNum)))
            }
    }

    private fun getBlockNumbers(pageSize: Int, lastBlockNum: suspend () -> Long): Flow<Long> {
        return flow {
            val lastBlockNumLong = lastBlockNum()
            (lastBlockNumLong downTo max(1, lastBlockNumLong - pageSize + 1)).map { blockNum ->
                emit(blockNum)
            }
        }
    }
}