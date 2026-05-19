package es.ejemplo.android.mybooklist.general.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): OpenLibraryResponse
}

data class OpenLibraryResponse(
    val docs: List<OpenLibraryDoc>?
)

data class OpenLibraryDoc(
    val title: String?,
    val author_name: List<String>?,
    val isbn: List<String>?,
    val first_publish_year: Int?,
    val cover_i: Int?,
    val number_of_pages_median: Int?,
    val subject: List<String>?
)
