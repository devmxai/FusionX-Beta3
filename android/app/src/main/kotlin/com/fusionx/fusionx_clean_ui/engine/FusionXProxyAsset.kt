package com.fusionx.fusionx_clean_ui.engine

data class FusionXProxyAsset(
    val path: String,
    val shortSide: Int,
    val sourceDurationUs: Long,
    val proxyDurationUs: Long,
    val sourceFrameTimesUs: LongArray = LongArray(0),
    val proxyFrameTimesUs: LongArray = LongArray(0),
)
