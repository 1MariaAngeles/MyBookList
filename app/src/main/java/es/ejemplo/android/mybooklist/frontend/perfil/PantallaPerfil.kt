package es.ejemplo.android.mybooklist.frontend.perfil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import es.ejemplo.android.mybooklist.libros.ui.LibroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    viewModel: LibroViewModel,
    alHacerClicEnEstadistica: (String) -> Unit
) {
    val totalPaginas by viewModel.totalPaginasLeidas.collectAsState(initial = 0)
    val librosTerminados by viewModel.conteoLibrosTerminados.collectAsState(initial = 0)
    val generosMasLeidos by viewModel.generosMasLeidos.collectAsState(initial = emptyList())
    val meta by viewModel.metaLectura.collectAsState()
    val fotoPerfil by viewModel.fotoPerfilUri.collectAsState()
    val nombre by viewModel.nombreUsuario.collectAsState()

    var mostrarDialogoMeta by remember { mutableStateOf(false) }
    var mostrarDialogoNombre by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> 
        uri?.let { viewModel.actualizarFotoPerfil(it.toString()) }
    }

    val colorFondo = Color(0xFFFDFCF4)
    val verdePrincipal = Color(0xFF6B8E23)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Mi Perfil",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(vertical = 16.dp),
            color = Color(0xFF2D3436)
        )

        // Sección de Usuario
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = Color(0xFFDDE5B6)
                ) {
                    if (fotoPerfil != null) {
                        AsyncImage(
                            model = fotoPerfil,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(20.dp),
                            tint = verdePrincipal
                        )
                    }
                }
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = verdePrincipal,
                    shadowElevation = 4.dp
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(6.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { mostrarDialogoNombre = true }) {
                    Text(
                        text = nombre,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3436)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "Lector de MyBookList",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sección de Meta
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth().clickable { mostrarDialogoMeta = true }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Flag, contentDescription = null, tint = verdePrincipal)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Meta de lectura anual", fontWeight = FontWeight.Bold)
                        Text(text = "$meta libros este año", fontSize = 14.sp, color = Color.Gray)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Estadísticas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TarjetaEstadistica(
                titulo = "Páginas",
                valor = (totalPaginas ?: 0).toString(),
                icono = Icons.Default.AutoGraph,
                colorIcono = verdePrincipal,
                modifier = Modifier.weight(1f).clickable { alHacerClicEnEstadistica("paginas") }
            )
            Spacer(modifier = Modifier.width(16.dp))
            TarjetaEstadistica(
                titulo = "Leídos",
                valor = librosTerminados.toString(),
                icono = Icons.Default.CheckCircle,
                colorIcono = Color(0xFF8D6E63),
                modifier = Modifier.weight(1f).clickable { alHacerClicEnEstadistica("terminados") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Géneros
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth().clickable { alHacerClicEnEstadistica("generos") }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PieChart, contentDescription = null, tint = verdePrincipal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Tus géneros favoritos", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (generosMasLeidos.isEmpty()) {
                    Text("¡Empieza a leer para ver tus gustos!", color = Color.Gray, fontSize = 14.sp)
                } else {
                    generosMasLeidos.take(3).forEach { FilaGenero(it.genero, it.conteo) }
                }
            }
        }
    }

    if (mostrarDialogoNombre) {
        var nombreInput by remember { mutableStateOf(nombre) }
        AlertDialog(
            onDismissRequest = { mostrarDialogoNombre = false },
            title = { Text("¿Cómo te llamas?") },
            text = {
                OutlinedTextField(
                    value = nombreInput,
                    onValueChange = { if (it.length <= 15) nombreInput = it },
                    label = { Text("Nombre (máx 15 car.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.actualizarNombre(nombreInput)
                    mostrarDialogoNombre = false
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoNombre = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarDialogoMeta) {
        var metaInput by remember { mutableStateOf(meta.toString()) }
        AlertDialog(
            onDismissRequest = { mostrarDialogoMeta = false },
            title = { Text("Editar Meta Anual") },
            text = {
                OutlinedTextField(
                    value = metaInput,
                    onValueChange = { if (it.length <= 3) metaInput = it },
                    label = { Text("Número de libros") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.actualizarMeta(metaInput.toIntOrNull() ?: meta)
                    mostrarDialogoMeta = false
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoMeta = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun TarjetaEstadistica(titulo: String, valor: String, icono: androidx.compose.ui.graphics.vector.ImageVector, colorIcono: Color, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icono, null, tint = colorIcono)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = valor, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = titulo, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun FilaGenero(nombre: String, cantidad: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = nombre, fontSize = 14.sp)
        Text(text = cantidad.toString(), fontWeight = FontWeight.Bold, color = Color(0xFF6B8E23))
    }
}
