package es.ejemplo.android.mybooklist

import android.app.Application
import androidx.room.Room
import es.ejemplo.android.mybooklist.general.BaseDatosLocal
import es.ejemplo.android.mybooklist.general.UserPreferences
import es.ejemplo.android.mybooklist.general.remote.AniListService
import es.ejemplo.android.mybooklist.general.remote.GoogleBooksService
import es.ejemplo.android.mybooklist.general.remote.MangaDexService
import es.ejemplo.android.mybooklist.general.remote.OpenLibraryService
import es.ejemplo.android.mybooklist.libros.infraestructure.LibroRepositoryImpl
import es.ejemplo.android.mybooklist.libros.service.LibroService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyBookListApp : Application() {

    val baseDatos by lazy {
        Room.databaseBuilder(this, BaseDatosLocal::class.java, "mybooklist_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    val userPreferences by lazy { UserPreferences(this) }

    // Retrofit para Google Books
    private val retrofitGoogle by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit para Open Library
    private val retrofitOpenLibrary by lazy {
        Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit para AniList (GraphQL)
    private val retrofitAniList by lazy {
        Retrofit.Builder()
            .baseUrl("https://graphql.anilist.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit para MangaDex
    private val retrofitMangaDex by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mangadex.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val googleApi by lazy { retrofitGoogle.create(GoogleBooksService::class.java) }
    private val openLibraryApi by lazy { retrofitOpenLibrary.create(OpenLibraryService::class.java) }
    private val aniListApi by lazy { retrofitAniList.create(AniListService::class.java) }
    private val mangaDexApi by lazy { retrofitMangaDex.create(MangaDexService::class.java) }

    private val repositorio by lazy { 
        LibroRepositoryImpl(
            baseDatos.libroDao(), 
            googleApi, 
            openLibraryApi,
            aniListApi,
            mangaDexApi
        ) 
    }

    val libroService by lazy { LibroService(repositorio, baseDatos.reseniaDao()) }
}
