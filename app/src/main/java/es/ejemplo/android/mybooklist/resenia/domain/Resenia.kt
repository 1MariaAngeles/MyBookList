package es.ejemplo.android.mybooklist.resenia.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "Resenia")
data class Resenia (
    @PrimaryKey
    val idLibro: Int, // Un libro, una reseña
    val comentario: String,
    val ultimaEdicion: LocalDateTime = LocalDateTime.now()
)
