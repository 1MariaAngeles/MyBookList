package es.ejemplo.android.mybooklist.libros.dto.entrada

import es.ejemplo.android.mybooklist.libros.domain.Libro

interface ObtenerTodosLosLibros {
    fun obtenerTodosLosLibros(): List<Libro>
}
