package com.example.locationsaver.pojo.imageFromWeb

data class Value(
    val accentColor: String,
    val contentSize: String,
    val contentUrl: String,
    val creativeCommons: String,
    val datePublished: String,
    val encodingFormat: String,
    val height: Int,
    val hostPageDiscoveredDate: String,
    val hostPageDisplayUrl: String,
    val hostPageDomainFriendlyName: String,
    val hostPageFavIconUrl: String,
    val hostPageUrl: String,
    val imageId: String,
    val imageInsightsToken: String,
    val insightsMetadata: InsightsMetadata,
    val isFamilyFriendly: Boolean,
    val isFresh: Boolean,
    val name: String,
    val thumbnail: Thumbnail,
    val thumbnailUrl: String,
    val webSearchUrl: String,
    val width: Int

) {
    override fun toString(): String {
        return "Value(accentColor='$accentColor', " +
                "contentSize='$contentSize'," +
                " contentUrl='$contentUrl'" +
                ", creativeCommons='$creativeCommons', " +
                "datePublished='$datePublished', encodingFormat='$encodingFormat', " +
                "height=$height, hostPageDiscoveredDate='$hostPageDiscoveredDate', hostPageDisplayUrl='$hostPageDisplayUrl'," +
                " hostPageDomainFriendlyName='$hostPageDomainFriendlyName', hostPageFavIconUrl='$hostPageFavIconUrl', " +
                "hostPageUrl='$hostPageUrl', imageId='$imageId', imageInsightsToken='$imageInsightsToken', " +
                "insightsMetadata=$insightsMetadata, isFamilyFriendly=$isFamilyFriendly, isFresh=$isFresh, name='$name', " +
                "thumbnail=$thumbnail, thumbnailUrl='$thumbnailUrl', webSearchUrl='$webSearchUrl', width=$width)"
    }
}