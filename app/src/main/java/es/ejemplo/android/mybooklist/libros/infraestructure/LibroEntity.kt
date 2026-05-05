package es.ejemplo.android.mybooklist.libros.infraestructure

import androidx.room.Entity
import androidx.room.PrimaryKey
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import java.time.LocalDateTime

@Entity(tableName = "Libros")
data class LibroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val autor: String,
    val descripcion: String?,
    val isbn: String?,
    val portada: Int?,
    val paginasTotales: Int?,
    val paginasLeidas: Int = 0,
    val notaPersonal: Int?,
    val estados: Estados,
    val fechaCreacion: LocalDateTime? = null
)