package com.jaredzhang.blockviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jaredzhang.blockviewer.ui.blocklist.BlockListViewModel
import com.jaredzhang.blockviewer.utils.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: BlockListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(BlockListViewModel::class.java)

        viewModel.viewState.observe(this, Observer {
        })

        viewModel.fetchBlocks()
    }
}