package es.ejemplo.android.mybooklist.resenia.infraestructure

import androidx.room.Entity
import androidx.room.PrimaryKey
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import java.time.LocalDateTime

@Entity(tableName = "Resenia")
data class ReseniaEntity(
    @PrimaryKey(autoGenerate = true)
    val idResenia: Int,
    val idLibro: Int,
    val comentario: String,
    val ultimaEdicion: LocalDateTime
)