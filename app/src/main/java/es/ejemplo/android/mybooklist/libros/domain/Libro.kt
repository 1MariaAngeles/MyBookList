package es.ejemplo.android.mybooklist.libros.domain

import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import java.time.LocalDateTime

data class Libro(
    val id: Int,
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