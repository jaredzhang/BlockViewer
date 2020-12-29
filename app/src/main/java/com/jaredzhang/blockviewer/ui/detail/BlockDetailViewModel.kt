package com.jaredzhang.blockviewer.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.repository.ChainRepository
import com.jaredzhang.blockviewer.repository.Result
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
class BlockDetailViewModel @Inject constructor(
    private val repository: ChainRepository
): ViewModel(
) {
    private val internalViewState = MutableLiveData<ViewState>()
    private var gson = GsonBuilder().setPrettyPrinting().create()

    val viewState: LiveData<ViewState>
        get() = internalViewState

    fun fetchBlockDetail(blockNum: Long) {
        viewModelScope.launch {
            repository.getBlock(blockNum)
                .onStart { internalViewState.value = ViewState.Loading }
                .collect { result ->
                    when(result) {
                        is Result.Success -> {
                            internalViewState.value = ViewState.DataLoaded(result.data, gson.toJson(result.data))
                        }
                        is Result.Error -> internalViewState.value = ViewState.Error(result.error)
                    }
                }
        }
    }

}

sealed class ViewState {
    object Loading : ViewState()
    data class DataLoaded(val block: BlockInfo, val rawValue: String) : ViewState()
    data class Error(val error: Throwable) : ViewState()
}