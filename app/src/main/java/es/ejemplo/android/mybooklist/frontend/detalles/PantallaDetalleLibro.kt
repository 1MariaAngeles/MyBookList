package es.ejemplo.android.mybooklist.frontend.detalles

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import es.ejemplo.android.mybooklist.libros.ui.LibroViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantallaDetalleLibro(
    idLibro: Int,
    viewModel: LibroViewModel,
    alVolver: () -> Unit
) {
    val context = LocalContext.current
    val todosLosLibros by viewModel.todosLosLibros.collectAsState(initial = emptyList())
    val libro = todosLosLibros.find { it.id == idLibro }
    val resenia by viewModel.obtenerResenia(idLibro).collectAsState(initial = null)

    if (libro == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF6B8E23))
        }
        return
    }

    // Estados
    var comentario by remember(resenia) { mutableStateOf(resenia?.comentario ?: "") }
    var nuevoGenero by remember { mutableStateOf("") }
    var expandidoEstado by remember { mutableStateOf(false) }
    var mostrarDialogoProgresoPaginas by remember { mutableStateOf(false) }
    var mostrarDialogoProgresoCapitulos by remember { mutableStateOf(false) }
    var mostrarDialogoSinopsis by remember { mutableStateOf(false) }

    val launcherPortada = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.guardarLibro(libro.copy(portadaUrl = it.toString())) }
    }

    val launcherArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { 
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {}
            viewModel.guardarLibro(libro.copy(archivoUrl = it.toString())) 
        }
    }

    val colorFondo = Color(0xFFFDFCF4)
    val negroTexto = Color(0xFF000000)
    val grisOscuro = Color(0xFF333333)
    val verdePrincipal = Color(0xFF6B8E23)
    val verdeSuave = Color(0xFFDDE5B6)
    val colorMarron = Color(0xFF8D6E63)
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Función para mostrar el selector de fecha
    fun mostrarSelectorFecha(esInicio: Boolean) {
        val fechaActual = if (esInicio) libro.fechaInicio else libro.fechaFin
        val anio = fechaActual?.year ?: LocalDate.now().year
        val mes = (fechaActual?.monthValue ?: LocalDate.now().monthValue) - 1
        val dia = fechaActual?.dayOfMonth ?: LocalDate.now().dayOfMonth

        DatePickerDialog(context, { _, y, m, d ->
            val nuevaFecha = LocalDateTime.of(y, m + 1, d, 12, 0)
            if (esInicio) {
                viewModel.guardarLibro(libro.copy(fechaInicio = nuevaFecha))
            } else {
                viewModel.guardarLibro(libro.copy(fechaFin = nuevaFecha))
            }
        }, anio, mes, dia).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ficha Técnica", color = negroTexto, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = negroTexto)
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
            // Portada con edición
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .aspectRatio(0.7f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { launcherPortada.launch("image/*") },
                contentAlignment = Alignment.BottomEnd
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    AsyncImage(
                        model = libro.portadaUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Surface(
                    modifier = Modifier.size(36.dp).padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Cambiar portada", tint = Color.White, modifier = Modifier.padding(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = libro.titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = negroTexto)
            Text(text = "por ${libro.autor}", fontSize = 16.sp, color = grisOscuro)

            Spacer(modifier = Modifier.height(24.dp))

            // Estado y Valoración
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    FilterChip(
                        selected = true,
                        onClick = { expandidoEstado = true },
                        label = { Text("Estado: ${libro.estado}", fontWeight = FontWeight.Bold) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = verdeSuave,
                            labelColor = verdePrincipal
                        )
                    )
                    DropdownMenu(
                        expanded = expandidoEstado, 
                        onDismissRequest = { expandidoEstado = false },
                        modifier = Modifier.background(verdeSuave)
                    ) {
                        Estados.entries.forEach { est ->
                            DropdownMenuItem(
                                text = { Text(est.name, color = negroTexto) },
                                onClick = {
                                    viewModel.cambiarEstado(libro, est)
                                    expandidoEstado = false
                                }
                            )
                        }
                    }
                }

                Row {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= (libro.notaPersonal ?: 0)) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Nota $i",
                            tint = if (i <= (libro.notaPersonal ?: 0)) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { viewModel.guardarLibro(libro.copy(notaPersonal = i)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Archivo del Libro
            Text("Archivo Digital", fontWeight = FontWeight.Bold, color = negroTexto, modifier = Modifier.fillMaxWidth())
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (libro.archivoUrl != null) Icons.Default.Description else Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = if (libro.archivoUrl != null) verdePrincipal else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (libro.archivoUrl != null) "Archivo vinculado" else "Sin archivo adjunto",
                            fontSize = 14.sp,
                            color = if (libro.archivoUrl != null) negroTexto else Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row {
                        if (libro.archivoUrl != null) {
                            IconButton(onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(Uri.parse(libro.archivoUrl), "*/*")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Error al abrir
                                }
                            }) {
                                Icon(Icons.Default.OpenInNew, contentDescription = "Abrir archivo", tint = verdePrincipal)
                            }
                        }
                        IconButton(onClick = { launcherArchivo.launch("*/*") }) {
                            Icon(Icons.Default.AttachFile, contentDescription = "Vincular archivo", tint = verdePrincipal)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fechas de lectura EDITABLES
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.clickable { mostrarSelectorFecha(true) }
                ) {
                    Text("Inicio", fontSize = 12.sp, color = grisOscuro)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = libro.fechaInicio?.format(dateFormatter) ?: "Establecer",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (libro.fechaInicio == null) verdePrincipal else negroTexto
                        )
                        Icon(Icons.Default.EditCalendar, null, modifier = Modifier.size(16.dp).padding(start = 4.dp), tint = Color.Gray)
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.clickable { mostrarSelectorFecha(false) }
                ) {
                    Text("Fin", fontSize = 12.sp, color = grisOscuro)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EditCalendar, null, modifier = Modifier.size(16.dp).padding(end = 4.dp), tint = Color.Gray)
                        Text(
                            text = libro.fechaFin?.format(dateFormatter) ?: "Establecer",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (libro.fechaFin == null) verdePrincipal else negroTexto
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sinopsis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sinopsis", fontWeight = FontWeight.Bold, color = negroTexto)
                IconButton(onClick = { mostrarDialogoSinopsis = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar sinopsis", tint = verdePrincipal, modifier = Modifier.size(20.dp))
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { mostrarDialogoSinopsis = true },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text = libro.descripcion ?: "Sin descripción disponible.",
                    fontSize = 14.sp,
                    color = negroTexto,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Géneros (Añadir/Quitar)
            Text("Géneros", fontWeight = FontWeight.Bold, color = negroTexto, modifier = Modifier.fillMaxWidth())
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                libro.generos.forEach { gen ->
                    SuggestionChip(
                        onClick = { 
                            val lista = libro.generos.toMutableList().apply { remove(gen) }
                            viewModel.guardarLibro(libro.copy(generos = lista))
                        },
                        label = { Text(gen, color = negroTexto) },
                        icon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp), tint = negroTexto) }
                    )
                }
            }
            
            OutlinedTextField(
                value = nuevoGenero,
                onValueChange = { nuevoGenero = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Añadir nuevo género...", color = Color.Gray) },
                textStyle = TextStyle(color = negroTexto),
                trailingIcon = {
                    IconButton(onClick = {
                        if (nuevoGenero.isNotBlank()) {
                            val listaNueva = libro.generos.toMutableList().apply { add(nuevoGenero.trim()) }
                            viewModel.guardarLibro(libro.copy(generos = listaNueva))
                            nuevoGenero = ""
                        }
                    }) {
                        Icon(Icons.Default.AddCircle, null, tint = verdePrincipal)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdePrincipal)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // PROGRESO DUAL
            Text("Tu progreso", fontWeight = FontWeight.Bold, color = negroTexto, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { mostrarDialogoProgresoPaginas = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = colorMarron),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Páginas", fontSize = 12.sp, color = Color.White)
                        Text("${libro.paginasLeidas}/${libro.paginasTotales ?: "?"}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Button(
                    onClick = { mostrarDialogoProgresoCapitulos = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF607D8B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Capítulos", fontSize = 12.sp, color = Color.White)
                        Text("${libro.capitulosLeidos}/${libro.capitulosTotales ?: "?"}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Reseña Personal
            Text("Tu reseña", fontWeight = FontWeight.Bold, color = negroTexto, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = comentario,
                onValueChange = { 
                    comentario = it
                    viewModel.guardarResenia(libro.id, it)
                },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                textStyle = TextStyle(color = negroTexto),
                placeholder = { Text("Escribe aquí tu opinión...", color = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdePrincipal,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
    }

    // --- DIÁLOGOS DE ACTUALIZACIÓN ---

    if (mostrarDialogoProgresoPaginas) {
        var paginasLeidasInput by remember { mutableStateOf(libro.paginasLeidas.toString()) }
        var paginasTotalesInput by remember { mutableStateOf(libro.paginasTotales?.toString() ?: "") }

        AlertDialog(
            onDismissRequest = { mostrarDialogoProgresoPaginas = false },
            containerColor = verdeSuave,
            title = { Text("Actualizar páginas", color = negroTexto) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = paginasTotalesInput,
                        onValueChange = { paginasTotalesInput = it },
                        label = { Text("Páginas totales") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(color = negroTexto)
                    )
                    OutlinedTextField(
                        value = paginasLeidasInput,
                        onValueChange = { paginasLeidasInput = it },
                        label = { Text("¿Por qué página vas?") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(color = negroTexto)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val total = paginasTotalesInput.toIntOrNull()
                    val leidas = paginasLeidasInput.toIntOrNull() ?: 0
                    viewModel.actualizarProgreso(libro, leidas, total)
                    mostrarDialogoProgresoPaginas = false
                }) { Text("Guardar", color = verdePrincipal) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoProgresoPaginas = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }

    if (mostrarDialogoProgresoCapitulos) {
        var capsLeidosInput by remember { mutableStateOf(libro.capitulosLeidos.toString()) }
        var capsTotalesInput by remember { mutableStateOf(libro.capitulosTotales?.toString() ?: "") }

        AlertDialog(
            onDismissRequest = { mostrarDialogoProgresoCapitulos = false },
            containerColor = verdeSuave,
            title = { Text("Actualizar capítulos", color = negroTexto) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = capsTotalesInput,
                        onValueChange = { capsTotalesInput = it },
                        label = { Text("Capítulos totales") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(color = negroTexto)
                    )
                    OutlinedTextField(
                        value = capsLeidosInput,
                        onValueChange = { capsLeidosInput = it },
                        label = { Text("¿Por qué capítulo vas?") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(color = negroTexto)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val total = capsTotalesInput.toIntOrNull()
                    val leidos = capsLeidosInput.toIntOrNull() ?: 0
                    viewModel.actualizarProgresoCapitulos(libro, leidos, total)
                    mostrarDialogoProgresoCapitulos = false
                }) { Text("Guardar", color = verdePrincipal) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoProgresoCapitulos = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }

    if (mostrarDialogoSinopsis) {
        var sinopsisInput by remember { mutableStateOf(libro.descripcion ?: "") }
        AlertDialog(
            onDismissRequest = { mostrarDialogoSinopsis = false },
            containerColor = verdeSuave,
            title = { Text("Editar Sinopsis", color = negroTexto) },
            text = {
                OutlinedTextField(
                    value = sinopsisInput,
                    onValueChange = { sinopsisInput = it },
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    textStyle = TextStyle(color = negroTexto),
                    placeholder = { Text("Escribe la sinopsis...", color = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = verdePrincipal,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.guardarLibro(libro.copy(descripcion = sinopsisInput))
                    mostrarDialogoSinopsis = false
                }) { Text("Guardar", color = verdePrincipal) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoSinopsis = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }
}
