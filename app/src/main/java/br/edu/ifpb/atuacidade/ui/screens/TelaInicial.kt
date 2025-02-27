package br.edu.ifpb.atuacidade.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
//import br.edu.ifpb.atuacidade.ui.TelaCriacaoPost


@Composable
fun TelaInicial(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Bem-vindo!", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = { navController.navigate("cadastro") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Tela Cadastro") }

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Tela Login") }

        Button(
            onClick = { navController.navigate("perfil") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Tela Perfil") }

        Button(
            onClick = { navController.navigate("criacaoPost") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Tela Criação de Post") }

        Button(
            onClick = { navController.navigate("principal") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Tela Principal") }
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
        composable("login") { TelaLogin(navController) }
        composable("perfil") { TelaPerfil(navController) }
        composable("cadastro") { TelaCadastro(navController) }
        composable("criacaoPost") {
            TelaCriacaoPost(
                onPostCreated = { post ->
                    println("Post criado: $post")
                    // Voltar para a tela anterior após criar o post
                    navController.popBackStack()
                }
            )
        }
        composable("principal") { TelaPrincipal(navController) }  // Aqui, adicionando a tela principal
    }
}

