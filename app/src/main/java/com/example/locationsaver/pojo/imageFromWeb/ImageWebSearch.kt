package com.example.locationsaver.pojo.imageFromWeb

data class ImageWebSearch(
    val currentOffset: Int,
    val instrumentation: Instrumentation,
    val nextOffset: Int,
    val pivotSuggestions: List<PivotSuggestion>,
    val queryContext: QueryContext,
    val readLink: String,
    val totalEstimatedMatches: Int,
    val type: String,
    val value: List<Value>,
    val webSearchUrl: String

    )
{

}