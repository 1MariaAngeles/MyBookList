package es.ejemplo.android.mybooklist.libros.infraestructure

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EstadosMapper {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromEstado(estado: Estados): String = estado.name

    @TypeConverter
    fun toEstado(value: String): Estados = Estados.valueOf(value)

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }
}