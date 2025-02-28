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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.edu.ifpb.atuacidade.ui.theme.AtuacidadeTheme
import com.google.firebase.FirebaseApp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import br.edu.ifpb.atuacidade.ui.screens.TelaCadastro
import br.edu.ifpb.atuacidade.ui.screens.TelaCriacaoPost
import br.edu.ifpb.atuacidade.ui.screens.TelaInicial
import br.edu.ifpb.atuacidade.ui.screens.TelaLogin
import br.edu.ifpb.atuacidade.ui.screens.TelaPerfil
import br.edu.ifpb.atuacidade.ui.screens.TelaPrincipal

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            AtuacidadeTheme {
                AppNavegacao()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavegacao() {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = "inicial",
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        }
    ) {
        composable("inicial") { TelaInicial(navController) }
        composable("cadastro") { TelaCadastro(navController) } // Passando o navController
        composable("login") { TelaLogin(navController) } // Passando o navController
        composable("perfil") { TelaPerfil(navController) } // Passando o navController
        composable("criacaoPost") {
            TelaCriacaoPost(
                onPostCreated = { post ->
                    println("Post criado: $post")
                    // Voltar para a tela anterior após criar o post
                    navController.popBackStack()
                }
            )
        }
        composable("principal") { TelaPrincipal(navController) } // Passando o navController
    }
}

