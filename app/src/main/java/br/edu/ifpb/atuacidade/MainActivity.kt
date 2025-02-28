package br.edu.ifpb.atuacidade

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.ifpb.atuacidade.ui.screens.*
import br.edu.ifpb.atuacidade.ui.theme.AtuacidadeTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            AtuacidadeTheme {
                Scaffold { paddingValues ->
                    AppNavegacao(modifier = Modifier.padding(paddingValues))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavegacao(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable("home") { TelaHome(navController, modifier) }
        composable("inicial") { TelaInicial(navController, modifier) }
        composable("cadastro") { TelaCadastro(navController, modifier) }
        composable("login") { TelaLogin(navController, modifier) }
        composable("perfil") { TelaPerfil(navController, modifier) }
        composable("postagens") {
            val viewModel: PostagemViewModel = viewModel()
            TelaPostagens(
                viewModel = viewModel,
                onSucesso = {
                    navController.navigate("principal") {
                        popUpTo("postagens") { inclusive = true }
                    }
                }
            )
        }
        composable("principal") { TelaPrincipal(navController, modifier) }
    }
}