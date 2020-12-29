package com.jaredzhang.blockviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jaredzhang.blockviewer.api.BlockInfo
import com.jaredzhang.blockviewer.databinding.ActivityMainBinding
import com.jaredzhang.blockviewer.databinding.ItemBlockBinding
import com.jaredzhang.blockviewer.ui.blocklist.BlockListViewModel
import com.jaredzhang.blockviewer.ui.blocklist.ViewState
import com.jaredzhang.blockviewer.ui.detail.BlockDetailActivity
import com.jaredzhang.blockviewer.utils.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.coroutines.FlowPreview
import java.text.NumberFormat
import javax.inject.Inject

@FlowPreview
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: BlockListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(BlockListViewModel::class.java)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchBlocks()
        }
        binding.rvBlockList.layoutManager = LinearLayoutManager(this)
        val adapter = BlockListAdapter {
            startActivity(BlockDetailActivity.getIntent(this, it.blockNum ?: 0))
        }
        binding.rvBlockList.adapter = adapter

        viewModel.viewState.observe(this, Observer { viewState ->
            when(viewState) {
                ViewState.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                }
                is ViewState.DataLoaded -> {
                    binding.swipeRefresh.isRefreshing = false
                    adapter.submitList(viewState.blocks)
                }
                is ViewState.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(applicationContext, R.string.error_get_blocks, Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.fetchBlocks()
    }
}

class BlockListAdapter(
    private val itemClicked: (BlockInfo) -> Unit
) : ListAdapter<BlockInfo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

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
        (holder as? BlockItemViewHolder)?.bindBlock(getItem(position))
    }
}

class BlockItemViewHolder(
    private val binding: ItemBlockBinding,
    private val itemClicked: (BlockInfo) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var blockInfo: BlockInfo? = null

    companion object {
        val numberFormat: NumberFormat = NumberFormat.getNumberInstance()
    }

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