package es.ejemplo.android.mybooklist.frontend.biblioteca

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun PantallaBiblioteca(
    viewModel: LibroViewModel,
    alHacerClicEnAgregar: () -> Unit,
    alHacerClicEnLibro: (Int) -> Unit
) {
    val todosLosLibros by viewModel.todosLosLibros.collectAsState(initial = emptyList())
    var estadoFiltro by remember { mutableStateOf<Estados?>(null) }
    val fotoPerfil by viewModel.fotoPerfilUri.collectAsState()

    val librosMostrados = if (estadoFiltro == null) {
        todosLosLibros
    } else {
        todosLosLibros.filter { it.estado == estadoFiltro }
    }

    val colorFondo = Color(0xFFFDFCF4)
    val negroTexto = Color(0xFF000000)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = alHacerClicEnAgregar,
                containerColor = Color(0xFFC3D4E0),
                contentColor = Color(0xFF2D3436),
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        },
        containerColor = colorFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // Cabecera
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MyBookList",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = negroTexto
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

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                item {
                    ChipFiltro(
                        texto = "Todos",
                        seleccionado = estadoFiltro == null,
                        alHacerClic = { estadoFiltro = null },
                        negroTexto = negroTexto
                    )
                }
                items(Estados.entries) { estado ->
                    ChipFiltro(
                        texto = when(estado) {
                            Estados.Pendiente -> "Pendientes"
                            Estados.Leyendo -> "Leyendo"
                            Estados.Leido -> "Leídos"
                            Estados.Abandonado -> "Abandonados"
                        },
                        icono = when(estado) {
                            Estados.Pendiente -> Icons.Default.Schedule
                            Estados.Leyendo -> Icons.Default.MenuBook
                            Estados.Leido -> Icons.Default.Check
                            else -> null
                        },
                        seleccionado = estadoFiltro == estado,
                        alHacerClic = { 
                            estadoFiltro = if (estadoFiltro == estado) null else estado 
                        },
                        negroTexto = negroTexto
                    )
                }
            }

            if (librosMostrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu biblioteca está vacía", color = negroTexto, fontWeight = FontWeight.Medium)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(librosMostrados) { libro ->
                        CapaLibro(libro, alHacerClic = { alHacerClicEnLibro(libro.id) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipFiltro(
    texto: String,
    seleccionado: Boolean,
    alHacerClic: () -> Unit,
    icono: ImageVector? = null,
    negroTexto: Color
) {
    FilterChip(
        selected = seleccionado,
        onClick = alHacerClic,
        label = { Text(texto, fontSize = 14.sp, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal) },
        leadingIcon = icono?.let { 
            { Icon(it, contentDescription = null, modifier = Modifier.size(18.dp)) } 
        },
        shape = RoundedCornerShape(25.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFDDE5B6),
            selectedLabelColor = Color(0xFF6B8E23),
            containerColor = Color.White,
            labelColor = negroTexto
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = seleccionado,
            borderColor = if (seleccionado) Color.Transparent else Color(0xFFE0E0E0)
        )
    )
}

@Composable
fun CapaLibro(libro: Libro, alHacerClic: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable { alHacerClic() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        AsyncImage(
            model = libro.portadaUrl,
            contentDescription = libro.titulo,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
