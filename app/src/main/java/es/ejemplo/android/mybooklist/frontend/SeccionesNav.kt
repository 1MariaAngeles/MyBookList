package es.ejemplo.android.mybooklist.frontend

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class SeccionesNav(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
) {
    object Inicio : SeccionesNav("inicio", "Inicio", Icons.Default.Home)
    object Biblioteca : SeccionesNav("biblioteca", "Biblioteca", Icons.Default.LibraryBooks)
    object Buscar : SeccionesNav("buscar", "Buscar", Icons.Default.Search)
    object Perfil : SeccionesNav("perfil", "Perfil", Icons.Default.Person)
}
