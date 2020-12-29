package com.jaredzhang.blockviewer.api

import com.google.gson.annotations.SerializedName

data class BlockRequest(
    @SerializedName("block_num_or_id") private val blockNumber: Long
)