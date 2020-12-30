package com.jaredzhang.blockviewer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.databinding.ActivityMainBinding
import com.jaredzhang.blockviewer.databinding.ItemBlockBinding
import com.jaredzhang.blockviewer.ui.blocklist.BlockListViewModel
import com.jaredzhang.blockviewer.ui.detail.BlockDetailActivity
import com.jaredzhang.blockviewer.utils.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: BlockListViewModel
    private lateinit var adapter: BlockListAdapter

    private var fetchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(BlockListViewModel::class.java)

        binding.swipeRefresh.setOnRefreshListener {
            fetch()
        }
        binding.rvBlockList.layoutManager = LinearLayoutManager(this)

        adapter = BlockListAdapter {
            startActivity(BlockDetailActivity.getIntent(this, it.blockNum ?: 0))
        }
        binding.rvBlockList.adapter = adapter

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                when(loadStates.refresh) {
                    is LoadState.Loading -> {
                        binding.swipeRefresh.isRefreshing = true
                    }
                    is LoadState.Error -> {
                        Toast.makeText(applicationContext, R.string.error_get_blocks, Toast.LENGTH_LONG).show()
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is LoadState.NotLoading -> {
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        }

        lifecycleScope.launch {
            // Scroll to top when the list is refreshed from network.
            adapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.rvBlockList.scrollToPosition(0) }
        }

        fetch()
    }

    private fun fetch() {
        fetchJob?.cancel()
        fetchJob = lifecycleScope.launch {
            viewModel.fetchBlocks().collectLatest {
                adapter.submitData(it)
            }
        }
    }
}

class BlockListAdapter(
    private val itemClicked: (BlockInfo) -> Unit
) : PagingDataAdapter<BlockInfo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<BlockInfo>() {
            override fun areItemsTheSame(oldItem: BlockInfo, newItem: BlockInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BlockInfo, newItem: BlockInfo): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlockItemViewHolder(binding, itemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let{
            (holder as? BlockItemViewHolder)?.bindBlock(it)
        }
    }
}

class BlockItemViewHolder(
    private val binding: ItemBlockBinding,
    private val itemClicked: (BlockInfo) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var blockInfo: BlockInfo? = null

    init {
        itemView.setOnClickListener {
            blockInfo?.let { itemClicked(it) }
        }
    }

    fun bindBlock(block: BlockInfo) {
        this.blockInfo = block
        binding.tvBlockId.text = block.id
        binding.tvBlockNumber.text = block.blockNum?.toString()
    }
}