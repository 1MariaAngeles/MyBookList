package es.ejemplo.android.mybooklist.general.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20
    ): GoogleBooksResponse
}
