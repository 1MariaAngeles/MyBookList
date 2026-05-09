package es.ejemplo.android.mybooklist.frontend.detalles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import es.ejemplo.android.mybooklist.libros.ui.LibroViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantallaDetalleLibro(
    idLibro: Int,
    viewModel: LibroViewModel,
    alVolver: () -> Unit
) {
    val todosLosLibros by viewModel.todosLosLibros.collectAsState(initial = emptyList())
    val libro = todosLosLibros.find { it.id == idLibro }
    val resenia by viewModel.obtenerResenia(idLibro).collectAsState(initial = null)

    if (libro == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var comentario by remember(resenia) { mutableStateOf(resenia?.comentario ?: "") }
    var nuevoGenero by remember { mutableStateOf("") }
    var mostrarDialogoProgreso by remember { mutableStateOf(false) }
    var expandidoEstado by remember { mutableStateOf(false) }

    val colorFondo = Color(0xFFFDFCF4)
    val colorMarron = Color(0xFF8D6E63)
    val verdePrincipal = Color(0xFF6B8E23)
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ficha Técnica") },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.eliminarLibro(libro.id)
                        alVolver()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorFondo)
            )
        },
        containerColor = colorFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Portada
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(12.dp),
                modifier = Modifier.width(180.dp).aspectRatio(0.7f)
            ) {
                AsyncImage(
                    model = libro.portadaUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = libro.titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(text = "por ${libro.autor}", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Estado de lectura
            Box {
                FilterChip(
                    selected = true,
                    onClick = { expandidoEstado = true },
                    label = { Text("Estado: ${libro.estado}") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFFDDE5B6),
                        labelColor = verdePrincipal
                    )
                )
                DropdownMenu(expanded = expandidoEstado, onDismissRequest = { expandidoEstado = false }) {
                    Estados.entries.forEach { est ->
                        DropdownMenuItem(
                            text = { Text(est.name) },
                            onClick = {
                                viewModel.cambiarEstado(libro, est)
                                expandidoEstado = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fechas de lectura
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Inicio", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = libro.fechaInicio?.format(dateFormatter) ?: "No empezado",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Fin", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = libro.fechaFin?.format(dateFormatter) ?: "En progreso",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Información Adicional (Sinopsis)
            Text("Sinopsis", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!libro.isbn.isNullOrBlank()) {
                        Text(text = "ISBN: ${libro.isbn}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = libro.descripcion ?: "Sin descripción disponible.",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Géneros
            Text("Géneros", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                libro.generos.forEach { gen ->
                    SuggestionChip(
                        onClick = { 
                            val listaNueva = libro.generos.toMutableList().apply { remove(gen) }
                            viewModel.guardarLibro(libro.copy(generos = listaNueva))
                        },
                        label = { Text(gen) },
                        icon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp)) }
                    )
                }
            }
            
            OutlinedTextField(
                value = nuevoGenero,
                onValueChange = { nuevoGenero = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Añadir género...") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (nuevoGenero.isNotBlank()) {
                            val listaNueva = libro.generos.toMutableList().apply { add(nuevoGenero.trim()) }
                            viewModel.guardarLibro(libro.copy(generos = listaNueva))
                            nuevoGenero = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, null, tint = verdePrincipal)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdePrincipal)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Progreso
            Button(
                onClick = { mostrarDialogoProgreso = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorMarron),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Actualizar Progreso (${libro.paginasLeidas}/${libro.paginasTotales ?: "?"} págs)")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Valoración
            Text("Tu valoración", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 1..5) {
                    IconButton(onClick = { viewModel.guardarLibro(libro.copy(notaPersonal = i)) }) {
                        Icon(
                            imageVector = if (i <= (libro.notaPersonal ?: 0)) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = if (i <= (libro.notaPersonal ?: 0)) Color(0xFFFFD700) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reseña
            Text("Tu reseña", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            OutlinedTextField(
                value = comentario,
                onValueChange = { 
                    comentario = it
                    viewModel.guardarResenia(libro.id, it)
                },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text("Escribe aquí tu opinión personal...") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorMarron,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
    }

    if (mostrarDialogoProgreso) {
        var paginasInput by remember { mutableStateOf(libro.paginasLeidas.toString()) }
        AlertDialog(
            onDismissRequest = { mostrarDialogoProgreso = false },
            title = { Text("Registrar avance") },
            text = {
                OutlinedTextField(
                    value = paginasInput,
                    onValueChange = { paginasInput = it },
                    label = { Text("¿En qué página estás?") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.actualizarProgreso(libro, paginasInput.toIntOrNull() ?: 0)
                    mostrarDialogoProgreso = false
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoProgreso = false }) { Text("Cancelar") }
            }
        )
    }
}
