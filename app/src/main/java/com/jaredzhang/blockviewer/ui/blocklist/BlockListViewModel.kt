package com.jaredzhang.blockviewer.ui.blocklist

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.repository.ChainRepository
import com.jaredzhang.blockviewer.repository.Result
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
class BlockListViewModel @Inject constructor(
    private val repository: ChainRepository
): ViewModel(
) {

    companion object {
        const val RECENT_BLOCKS = 20;
    }

    private val internalViewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = internalViewState

    fun fetchBlocks() {
        viewModelScope.launch {
            val list = repository.getRecentBlocks(RECENT_BLOCKS)
                .onStart { internalViewState.value = ViewState.Loading }
                .onEach { Log.d("blocktest", "blocktest $it") }
                .filterIsInstance<Result.Success<BlockInfo>>()
                .map { it.data }
                .toList()

            if (list.isEmpty()) {
                internalViewState.value = ViewState.Error(Throwable("Could not fetch blocks"))
            } else {
                internalViewState.value = ViewState.DataLoaded(list)
            }
        }
    }

}

sealed class ViewState {
    object Loading : ViewState()
    data class DataLoaded(val blocks: List<BlockInfo>) : ViewState()
    data class Error(val error: Throwable) : ViewState()
}