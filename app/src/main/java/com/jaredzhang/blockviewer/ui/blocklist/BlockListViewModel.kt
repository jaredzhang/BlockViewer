package com.jaredzhang.blockviewer.ui.blocklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.data.ChainRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@FlowPreview
class BlockListViewModel @Inject constructor(
    private val repository: ChainRepository
): ViewModel(
) {

    fun fetchBlocks(): Flow<PagingData<BlockInfo>> {
        return repository.getBlocks()
            .cachedIn(viewModelScope)
    }

}