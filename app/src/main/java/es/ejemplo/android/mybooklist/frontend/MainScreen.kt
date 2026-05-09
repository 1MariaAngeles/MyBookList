package es.ejemplo.android.mybooklist.frontend

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import es.ejemplo.android.mybooklist.frontend.biblioteca.PantallaBiblioteca
import es.ejemplo.android.mybooklist.frontend.inicio.PantallaInicio
import es.ejemplo.android.mybooklist.frontend.buscar.PantallaBuscar
import es.ejemplo.android.mybooklist.frontend.buscar.PantallaEntradaManual
import es.ejemplo.android.mybooklist.frontend.perfil.PantallaPerfil
import es.ejemplo.android.mybooklist.frontend.perfil.PantallaDetalleEstadisticas
import es.ejemplo.android.mybooklist.frontend.detalles.PantallaDetalleLibro
import es.ejemplo.android.mybooklist.libros.ui.LibroViewModel

@Composable
fun MainScreen(viewModel: LibroViewModel) {
    val navController = rememberNavController()
    val secciones = listOf(
        SeccionesNav.Inicio,
        SeccionesNav.Biblioteca,
        SeccionesNav.Buscar,
        SeccionesNav.Perfil
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            NavigationBar(
                containerColor = Color(0xFFE9EDC9).copy(alpha = 0.9f),
                tonalElevation = 8.dp
            ) {
                secciones.forEach { seccion ->
                    NavigationBarItem(
                        icon = { Icon(seccion.icono, contentDescription = seccion.titulo) },
                        label = { Text(seccion.titulo) },
                        selected = currentDestination?.hierarchy?.any { it.route == seccion.ruta } == true,
                        onClick = {
                            navController.navigate(seccion.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SeccionesNav.Inicio.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(SeccionesNav.Inicio.ruta) {
                PantallaInicio(
                    viewModel = viewModel,
                    alHacerClicEnLibro = { id -> navController.navigate("detalle/$id") }
                )
            }
            composable(SeccionesNav.Biblioteca.ruta) {
                PantallaBiblioteca(
                    viewModel = viewModel, 
                    alHacerClicEnAgregar = { navController.navigate(SeccionesNav.Buscar.ruta) },
                    alHacerClicEnLibro = { id -> navController.navigate("detalle/$id") }
                )
            }
            composable(SeccionesNav.Buscar.ruta) {
                PantallaBuscar(
                    viewModel = viewModel,
                    alHacerClicEnManual = { navController.navigate("entrada_manual") },
                    alHacerClicEnLibro = { id -> navController.navigate("detalle/$id") },
                    alVolver = { navController.popBackStack() }
                )
            }
            composable(SeccionesNav.Perfil.ruta) {
                PantallaPerfil(
                    viewModel = viewModel,
                    alHacerClicEnEstadistica = { tipo -> navController.navigate("estadistica_detalle/$tipo") }
                )
            }
            composable(
                route = "detalle/{libroId}",
                arguments = listOf(navArgument("libroId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("libroId") ?: 0
                PantallaDetalleLibro(
                    idLibro = id,
                    viewModel = viewModel,
                    alVolver = { navController.popBackStack() }
                )
            }
            composable("entrada_manual") {
                PantallaEntradaManual(
                    viewModel = viewModel,
                    alVolver = { navController.popBackStack() }
                )
            }
            composable(
                route = "estadistica_detalle/{tipo}",
                arguments = listOf(navArgument("tipo") { type = NavType.StringType })
            ) { backStackEntry ->
                val tipo = backStackEntry.arguments?.getString("tipo") ?: ""
                PantallaDetalleEstadisticas(
                    tipo = tipo,
                    viewModel = viewModel,
                    alVolver = { navController.popBackStack() }
                )
            }
        }
    }
}
