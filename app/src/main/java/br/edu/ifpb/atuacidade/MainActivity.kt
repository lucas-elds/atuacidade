package br.edu.ifpb.atuacidade

import PostagemUtil
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.ifpb.atuacidade.ui.composables.screens.TelaCadastro
import br.edu.ifpb.atuacidade.ui.composables.screens.TelaEditarDados
import br.edu.ifpb.atuacidade.ui.composables.screens.TelaHome
import br.edu.ifpb.atuacidade.ui.composables.screens.TelaLogin
import br.edu.ifpb.atuacidade.ui.composables.screens.TelaPerfil
import br.edu.ifpb.atuacidade.ui.composables.screens.TelaPostagens
import br.edu.ifpb.atuacidade.ui.composables.screens.TelaPrincipal
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
        composable("home") { TelaHome(navController) }
        composable("cadastro") { TelaCadastro(navController) }
        composable("login") { TelaLogin(navController) }
        composable("perfil") { TelaPerfil(navController) }
        composable("editar_dados") { TelaEditarDados(navController) }
        composable("postagens") {
            val viewModel: PostagemUtil = viewModel()
            TelaPostagens(
                viewModel = viewModel,
                onSucesso = {
                    navController.navigate("principal") {
                        popUpTo("postagens") { inclusive = true }
                    }
                },
                onVoltar = { navController.popBackStack() }
            )
        }

        composable("principal") { TelaPrincipal(navController) }
    }
}