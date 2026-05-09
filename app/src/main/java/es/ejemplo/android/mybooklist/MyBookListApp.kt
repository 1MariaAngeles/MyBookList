package es.ejemplo.android.mybooklist

import android.app.Application
import androidx.room.Room
import es.ejemplo.android.mybooklist.general.BaseDatosLocal
import es.ejemplo.android.mybooklist.general.UserPreferences
import es.ejemplo.android.mybooklist.general.remote.GoogleBooksService
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

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val servicioApi by lazy { retrofit.create(GoogleBooksService::class.java) }

    private val repositorio by lazy { LibroRepositoryImpl(baseDatos.libroDao(), servicioApi) }

    val libroService by lazy { LibroService(repositorio, baseDatos.reseniaDao()) }
}
