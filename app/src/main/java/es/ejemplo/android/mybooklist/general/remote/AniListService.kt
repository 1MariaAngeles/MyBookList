package es.ejemplo.android.mybooklist.general.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AniListService {
    @POST("/")
    suspend fun searchManga(@Body request: AniListRequest): AniListResponse
}

data class AniListRequest(
    val query: String,
    val variables: Map<String, Any?>
)

data class AniListResponse(
    val data: AniListData?
)

data class AniListData(
    val Page: AniListPage?
)

data class AniListPage(
    val media: List<AniListMedia>?
)

data class AniListMedia(
    val id: Int,
    val title: AniListTitle?,
    val description: String?,
    val coverImage: AniListCover?,
    val chapters: Int?,
    val genres: List<String>?
)

data class AniListTitle(
    val romaji: String?,
    val english: String?,
    val native: String?
)

data class AniListCover(
    val large: String?
)
