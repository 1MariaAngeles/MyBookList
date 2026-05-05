package es.ejemplo.android.mybooklist.libros.dto.entrada

import es.ejemplo.android.mybooklist.libros.domain.Libro

interface GuardarLibro {
    fun guardarLibro(libro: Libro)
}