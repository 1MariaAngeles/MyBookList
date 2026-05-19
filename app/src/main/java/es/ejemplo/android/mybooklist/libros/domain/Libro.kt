package es.ejemplo.android.mybooklist.libros.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import java.time.LocalDateTime

@Entity(tableName = "Libros")
data class Libro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val autor: String,
    val descripcion: String? = null,
    val isbn: String? = null,
    val portadaUrl: String? = null,
    val paginasTotales: Int? = null,
    val generos: List<String> = emptyList(),
    val paginasLeidas: Int = 0,
    val capitulosTotales: Int? = null,
    val capitulosLeidos: Int = 0,
    val notaPersonal: Int? = null,
    val estado: Estados = Estados.Pendiente,
    val fechaPublicacion: String? = null,
    val fechaInicio: LocalDateTime? = null,
    val fechaFin: LocalDateTime? = null,
    val archivoUrl: String? = null
) {
    val porcentajeCompletado: Int
        get() {
            val porcPinas = if (paginasTotales != null && paginasTotales > 0) {
                (paginasLeidas.toFloat() / paginasTotales.toFloat()) * 100
            } else 0f
            
            val porcCapitulos = if (capitulosTotales != null && capitulosTotales > 0) {
                (capitulosLeidos.toFloat() / capitulosTotales.toFloat()) * 100
            } else 0f
            
            return maxOf(porcPinas, porcCapitulos).toInt().coerceAtMost(100)
        }
}
