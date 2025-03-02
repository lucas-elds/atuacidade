package br.edu.ifpb.atuacidade.ui.composables.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.atuacidade.model.service.PostagemViewModel


@Composable
fun TelaInicial(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
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
            onClick = { navController.navigate("postagens") }, // Alterado para "postagens"
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
        composable("login") { TelaLogin(navController, modifier) }
        composable("perfil") { TelaPerfil(navController, modifier) }
        composable("cadastro") { TelaCadastro(navController, modifier) }
        composable("postagens") {
            val viewModel: PostagemViewModel = viewModel()
            TelaPostagens(
                viewModel = viewModel,
                onSucesso = {
                    navController.navigate("principal") {
                        popUpTo("postagens") { inclusive = true }
                    }
                },
                onVoltar = { navController.popBackStack() } // Adiciona ação de voltar
            )
        }

        composable("principal") { TelaPrincipal(navController, modifier) }
    }
}




