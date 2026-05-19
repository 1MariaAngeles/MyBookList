package es.ejemplo.android.mybooklist.general.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface MangaDexService {
    @GET("manga")
    suspend fun searchManga(
        @Query("title") title: String,
        @Query("limit") limit: Int = 15,
        @Query("includes[]") includes: List<String> = listOf("cover_art", "author")
    ): MangaDexResponse
}

data class MangaDexResponse(
    val data: List<MangaDexData>?
)

data class MangaDexData(
    val id: String,
    val attributes: MangaDexAttributes?,
    val relationships: List<MangaDexRelationship>?
)

data class MangaDexAttributes(
    val title: Map<String, String>?,
    val description: Map<String, String>?,
    val lastChapter: String?,
    val status: String?
)

data class MangaDexRelationship(
    val id: String,
    val type: String,
    val attributes: MangaDexRelAttributes?
)

data class MangaDexRelAttributes(
    val fileName: String?,
    val name: String?
)
