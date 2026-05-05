package es.ejemplo.android.mybooklist.libros.infraestructure

import android.annotation.TargetApi
import android.os.Build
import androidx.room.TypeConverter
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@TargetApi(Build.VERSION_CODES.O)
class EstadosMapper {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // Conversores para el Enum Estados
    @TypeConverter
    fun fromEstado(estado: Estados): String = estado.name

    @TypeConverter
    fun toEstado(value: String): Estados = Estados.valueOf(value)

    // Conversores para LocalDateTime (Java 8+)
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }
}