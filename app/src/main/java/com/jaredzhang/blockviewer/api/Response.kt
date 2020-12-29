package com.jaredzhang.blockviewer.api

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class ChainInfo(
    @SerializedName("server_version")
    @Expose
    val serverVersion: String? = null,

    @SerializedName("chain_id")
    @Expose
    val chainId: String? = null,

    @SerializedName("head_block_num")
    @Expose
    val headBlockNum: Int? = null,

    @SerializedName("last_irreversible_block_num")
    @Expose
    val lastIrreversibleBlockNum: Int? = null,

    @SerializedName("last_irreversible_block_id")
    @Expose
    val lastIrreversibleBlockId: String? = null,

    @SerializedName("head_block_id")
    @Expose
    val headBlockId: String? = null,

    @SerializedName("head_block_time")
    @Expose
    val headBlockTime: String? = null,

    @SerializedName("head_block_producer")
    @Expose
    val headBlockProducer: String? = null,

    @SerializedName("virtual_block_cpu_limit")
    @Expose
    val virtualBlockCpuLimit: Int? = null,

    @SerializedName("virtual_block_net_limit")
    @Expose
    val virtualBlockNetLimit: Int? = null,

    @SerializedName("block_cpu_limit")
    @Expose
    val blockCpuLimit: Int? = null,

    @SerializedName("block_net_limit")
    @Expose
    val blockNetLimit: Int? = null,

    @SerializedName("server_version_string")
    @Expose
    val serverVersionString: String? = null,

    @SerializedName("fork_db_head_block_num")
    @Expose
    val forkDbHeadBlockNum: Int? = null,

    @SerializedName("fork_db_head_block_id")
    @Expose
    val forkDbHeadBlockId: String? = null,

    @SerializedName("server_full_version_string")
    @Expose
    val serverFullVersionString: String? = null
)

data class BlockInfo (
    @SerializedName("timestamp")
    @Expose
    val timestamp: String? = null,

    @SerializedName("producer")
    @Expose
    val producer: String? = null,

    @SerializedName("confirmed")
    @Expose
    val confirmed: Int? = null,

    @SerializedName("previous")
    @Expose
    val previous: String? = null,

    @SerializedName("transaction_mroot")
    @Expose
    val transactionMroot: String? = null,

    @SerializedName("action_mroot")
    @Expose
    val actionMroot: String? = null,

    @SerializedName("schedule_version")
    @Expose
    val scheduleVersion: Int? = null,

    @SerializedName("new_producers")
    @Expose
    val newProducers: Any? = null,

    @SerializedName("producer_signature")
    @Expose
    val producerSignature: String? = null,

    @SerializedName("transactions")
    @Expose
    val transactions: List<Any>? = null,

    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("block_num")
    @Expose
    val blockNum: Int? = null,

    @SerializedName("ref_block_prefix")
    @Expose
    val refBlockPrefix: Int? = null
)