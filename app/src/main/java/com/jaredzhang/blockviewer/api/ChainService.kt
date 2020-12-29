package com.jaredzhang.blockviewer.api

import retrofit2.http.Body
import retrofit2.http.POST

interface ChainService {
    @POST("/v1/chain/get_info")
    suspend fun info(): ChainInfo

    @POST("/v1/chain/get_block")
    suspend fun block(@Body blockRequest: BlockRequest): BlockInfo

}