package com.example.frontproject.data.model

import com.google.gson.annotations.SerializedName

data class BzuStatsResponse(
    val stats: List<BzuStat>
)

data class BzuStat(
    val time: String,
    @SerializedName("B") val b: Int,
    @SerializedName("Z") val z: Int,
    @SerializedName("U") val u: Int
)