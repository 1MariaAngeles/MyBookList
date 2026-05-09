package es.ejemplo.android.mybooklist.frontend.buscar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.ui.LibroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBuscar(
    viewModel: LibroViewModel,
    alHacerClicEnManual: () -> Unit,
    alHacerClicEnLibro: (Int) -> Unit,
    alVolver: () -> Unit
) {
    val consulta by viewModel.consultaBusqueda.collectAsState()
    val resultadosRemote by viewModel.resultadosBusqueda.collectAsState()
    val prediccionesLocales by viewModel.prediccionesLocales.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()
    val focusManager = LocalFocusManager.current

    val colorFondo = Color(0xFFFDFCF4)
    val verdePrincipal = Color(0xFF6B8E23)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explorar", fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Búsqueda con predicciones
            OutlinedTextField(
                value = consulta,
                onValueChange = { viewModel.actualizarConsulta(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Título o autor...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (consulta.isNotEmpty()) {
                        IconButton(onClick = { 
                            viewModel.buscarLibros(consulta)
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Buscar en Google", tint = verdePrincipal)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.buscarLibros(consulta)
                        focusManager.clearFocus()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdePrincipal,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Manual
            TextButton(
                onClick = alHacerClicEnManual,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Añadir manualmente", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Predicciones Locales
                if (prediccionesLocales.isNotEmpty()) {
                    item { Text("En tu biblioteca", fontWeight = FontWeight.Bold, color = verdePrincipal) }
                    items(prediccionesLocales) { libro ->
                        ItemResultadoBusqueda(libro, enBiblioteca = true, alAnadir = {}, alHacerClic = { alHacerClicEnLibro(libro.id) })
                    }
                }

                // Resultados de Google Books
                if (estaCargando) {
                    item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = verdePrincipal) } }
                } else if (resultadosRemote.isNotEmpty()) {
                    item { Text("Resultados de Google Books", fontWeight = FontWeight.Bold, color = Color.Gray) }
                    items(resultadosRemote) { libro ->
                        ItemResultadoBusqueda(
                            libro = libro,
                            enBiblioteca = false,
                            alAnadir = { viewModel.guardarLibro(libro) },
                            alHacerClic = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemResultadoBusqueda(
    libro: Libro,
    enBiblioteca: Boolean,
    alAnadir: () -> Unit,
    alHacerClic: () -> Unit
) {
    var guardado by remember { mutableStateOf(enBiblioteca) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable { if (enBiblioteca) alHacerClic() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp).height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = libro.portadaUrl,
                contentDescription = null,
                modifier = Modifier.width(55.dp).fillMaxHeight().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = libro.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                Text(text = libro.autor, fontSize = 13.sp, color = Color.Gray)
                if (enBiblioteca) {
                    Text(text = "Estado: ${libro.estado}", fontSize = 11.sp, color = Color(0xFF6B8E23))
                }
            }
            if (!enBiblioteca) {
                IconButton(onClick = { guardado = true; alAnadir() }, enabled = !guardado) {
                    Icon(
                        imageVector = if (guardado) Icons.Default.CheckCircle else Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = if (guardado) Color.Gray else Color(0xFF6B8E23)
                    )
                }
            }
        }
    }
}
