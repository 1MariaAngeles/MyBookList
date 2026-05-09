package es.ejemplo.android.mybooklist.frontend.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import es.ejemplo.android.mybooklist.libros.ui.LibroViewModel

@Composable
fun PantallaInicio(viewModel: LibroViewModel, alHacerClicEnLibro: (Int) -> Unit) {
    val todosLosLibros by viewModel.todosLosLibros.collectAsState(initial = emptyList())
    val librosLeyendo = todosLosLibros.filter { it.estado == Estados.Leyendo }
    
    val totalPaginas by viewModel.totalPaginasLeidas.collectAsState(initial = 0)
    val terminados by viewModel.conteoLibrosTerminados.collectAsState(initial = 0)
    val meta by viewModel.metaLectura.collectAsState()
    val fotoPerfil by viewModel.fotoPerfilUri.collectAsState()
    val nombre by viewModel.nombreUsuario.collectAsState()

    val colorFondo = Color(0xFFFDFCF4)
    val verdePrincipal = Color(0xFF6B8E23)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Cabecera Unificada
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MyBookList",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF2D3436)
            )
            Surface(
                modifier = Modifier.size(45.dp),
                shape = CircleShape,
                color = Color.LightGray
            ) {
                if (fotoPerfil != null) {
                    AsyncImage(
                        model = fotoPerfil,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                }
            }
        }

        Text(
            text = "Hola, $nombre,\n¿qué leemos hoy?",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 42.sp,
            color = Color(0xFF2D3436),
            maxLines = 3
        )

        // Mensaje de Meta cumplida
        if (terminados >= meta && meta > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color(0xFFDDE5B6),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = verdePrincipal, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "¡Increíble! Has alcanzado tu meta de $meta libros. 🏆",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3436)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Carrusel destacado
        if (librosLeyendo.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(librosLeyendo) { libro ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier
                            .size(width = 150.dp, height = 220.dp)
                            .clickable { alHacerClicEnLibro(libro.id) }
                    ) {
                        AsyncImage(
                            model = libro.portadaUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Tu progreso global",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EstadisticaSimple("Páginas", (totalPaginas ?: 0).toString())
            EstadisticaSimple("Leídos", terminados.toString())
            EstadisticaSimple("Meta", meta.toString())
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Continúa Leyendo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (librosLeyendo.isNotEmpty()) {
            librosLeyendo.forEach { libro ->
                ItemProgresoLibro(libro, verdePrincipal, alHacerClicEnLibro)
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            Text(text = "No tienes lecturas activas", color = Color.Gray)
        }
    }
}

@Composable
fun EstadisticaSimple(label: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF6B8E23))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ItemProgresoLibro(libro: Libro, colorBarra: Color, alHacerClicEnLibro: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { alHacerClicEnLibro(libro.id) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = libro.portadaUrl,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = libro.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "Progreso: ${libro.porcentajeCompletado}%", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { libro.porcentajeCompletado / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = colorBarra,
                trackColor = Color(0xFFE0E0E0)
            )
        }
    }
}
