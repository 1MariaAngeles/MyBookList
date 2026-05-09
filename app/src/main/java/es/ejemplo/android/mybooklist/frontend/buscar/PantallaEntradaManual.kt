package es.ejemplo.android.mybooklist.frontend.buscar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.ui.LibroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEntradaManual(
    viewModel: LibroViewModel,
    alVolver: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var paginas by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }

    val colorFondo = Color(0xFFFDFCF4)
    val verdePrincipal = Color(0xFF6B8E23)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Manualmente", fontFamily = FontFamily.Serif) },
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Datos del Libro",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436)
            )

            CampoEntrada(label = "Título*", valor = titulo, alCambiar = { titulo = it })
            CampoEntrada(label = "Autor*", valor = autor, alCambiar = { autor = it })
            CampoEntrada(label = "Géneros (separa por comas)", valor = genero, alCambiar = { genero = it })
            CampoEntrada(label = "ISBN", valor = isbn, alCambiar = { isbn = it })
            CampoEntrada(label = "Total de páginas", valor = paginas, alCambiar = { paginas = it }, esNumerico = true)
            
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdePrincipal,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (titulo.isNotBlank() && autor.isNotBlank()) {
                        val nuevoLibro = Libro(
                            titulo = titulo,
                            autor = autor,
                            descripcion = descripcion,
                            isbn = isbn,
                            portadaUrl = null,
                            paginasTotales = paginas.toIntOrNull(),
                            generos = genero.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        )
                        viewModel.guardarLibro(nuevoLibro)
                        alVolver()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal),
                shape = RoundedCornerShape(12.dp),
                enabled = titulo.isNotBlank() && autor.isNotBlank()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("GUARDAR LIBRO", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoEntrada(
    label: String,
    valor: String,
    alCambiar: (String) -> Unit,
    esNumerico: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = if (esNumerico) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF6B8E23),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}
