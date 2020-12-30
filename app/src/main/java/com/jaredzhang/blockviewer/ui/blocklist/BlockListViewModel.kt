package com.jaredzhang.blockviewer.ui.blocklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val internalViewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = internalViewState

    fun fetchBlocks(): Flow<PagingData<BlockInfo>> {
        return repository.getBlocks()
            .cachedIn(viewModelScope)
    }

}

sealed class ViewState {
    object Loading : ViewState()
    data class DataLoaded(val blocks: List<BlockInfo>) : ViewState()
    data class Error(val error: Throwable) : ViewState()
}