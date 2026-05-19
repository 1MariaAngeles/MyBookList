package es.ejemplo.android.mybooklist.frontend.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleEstadisticas(
    tipo: String,
    viewModel: LibroViewModel,
    alVolver: () -> Unit
) {
    val todosLosLibros by viewModel.todosLosLibros.collectAsState(initial = emptyList())
    
    val colorFondo = Color(0xFFFDFCF4)
    val verdePrincipal = Color(0xFF6B8E23)
    val negroTexto = Color(0xFF000000)
    val grisOscuro = Color(0xFF333333)

    val titulo = when (tipo) {
        "terminados" -> "Libros Terminados"
        "paginas" -> "Progreso de Lectura"
        "generos" -> "Libros por Género"
        else -> "Tus Libros"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo, fontFamily = FontFamily.Serif, color = negroTexto, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = negroTexto)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorFondo)
            )
        },
        containerColor = colorFondo
    ) { padding ->
        if (todosLosLibros.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay libros registrados", color = grisOscuro, fontWeight = FontWeight.Medium)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (tipo == "generos") {
                    val generosUnicos = todosLosLibros.flatMap { it.generos }.distinct().sorted()
                    
                    generosUnicos.forEach { genero ->
                        item {
                            Text(
                                text = genero,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = negroTexto,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        val librosDeEsteGenero = todosLosLibros.filter { it.generos.contains(genero) }
                        items(librosDeEsteGenero) { libro ->
                            ItemLibroEstadistica(libro, null, negroTexto, grisOscuro, verdePrincipal)
                        }
                    }
                } else {
                    val librosFiltrados = when (tipo) {
                        "terminados" -> todosLosLibros.filter { it.estado == Estados.Leido }
                        "paginas" -> todosLosLibros.filter { it.paginasLeidas > 0 }.sortedByDescending { it.paginasLeidas }
                        else -> todosLosLibros
                    }
                    items(librosFiltrados) { libro ->
                        ItemLibroEstadistica(libro, tipo, negroTexto, grisOscuro, verdePrincipal)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemLibroEstadistica(libro: Libro, tipo: String?, colorTitulo: Color, colorGris: Color, colorVerde: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp).height(85.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = libro.portadaUrl,
                contentDescription = null,
                modifier = Modifier.width(55.dp).fillMaxHeight().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = libro.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, color = colorTitulo)
                Text(text = "De ${libro.autor}", fontSize = 13.sp, color = colorGris)
                Spacer(modifier = Modifier.height(4.dp))
                if (tipo == "paginas") {
                    Text(
                        text = "${libro.paginasLeidas} / ${libro.paginasTotales ?: "?"} págs",
                        fontSize = 12.sp,
                        color = colorVerde,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(text = "Estado: ${libro.estado}", fontSize = 12.sp, color = colorVerde, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
