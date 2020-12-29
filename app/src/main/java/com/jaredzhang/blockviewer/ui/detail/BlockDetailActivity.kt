package com.jaredzhang.blockviewer.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jaredzhang.blockviewer.R
import com.jaredzhang.blockviewer.databinding.ActivityBlockDetailBinding
import com.jaredzhang.blockviewer.utils.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
class BlockDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: BlockDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = ActivityBlockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.switchRaw.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.tvBlockRawContent.visibility = View.VISIBLE
            } else {
                binding.tvBlockRawContent.visibility = View.GONE
            }
        }

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(BlockDetailViewModel::class.java)

        viewModel.viewState.observe(this, Observer { viewState ->
            when(viewState) {
                ViewState.Loading -> {
                }
                is ViewState.DataLoaded -> {
                    binding.tvBlockProducerContent.text = viewState.block.producer
                    binding.tvBlockTransactionNumber.text = (viewState.block.transactions?.size ?: 0).toString()
                    binding.tvBlockProducerSignature.text = viewState.block.producerSignature
                    binding.tvBlockRawContent.text = viewState.rawValue
                }
                is ViewState.Error -> {
                    Toast.makeText(applicationContext, R.string.error_get_block, Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.fetchBlockDetail(intent.getLongExtra(EXTRA_BLOCK_NUM, 0))
    }

    companion object {
        const val EXTRA_BLOCK_NUM = "EXTRA_BLOCK_NUM"

        fun getIntent(context: Context, blockNum: Long): Intent {
            return Intent(context, BlockDetailActivity::class.java).putExtra(EXTRA_BLOCK_NUM, blockNum)
        }
    }
}
