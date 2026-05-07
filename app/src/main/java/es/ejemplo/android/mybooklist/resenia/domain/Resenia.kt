package es.ejemplo.android.mybooklist.resenia.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "Resenia")
data class Resenia (
    @PrimaryKey(autoGenerate = true)
    val idResenia: Int = 0,
    val idLibro: Int,
    val comentario: String,
    val ultimaEdicion: LocalDateTime = LocalDateTime.now()
)
