package es.ejemplo.android.mybooklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import es.ejemplo.android.mybooklist.frontend.MainScreen
import es.ejemplo.android.mybooklist.libros.ui.LibroViewModel
import es.ejemplo.android.mybooklist.ui.theme.MyBookListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val app = application as MyBookListApp
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LibroViewModel(app.libroService, app.userPreferences) as T
            }
        }

        setContent {
            MyBookListTheme {
                val viewModel: LibroViewModel = viewModel(factory = factory)
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
