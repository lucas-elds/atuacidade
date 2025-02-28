package br.edu.ifpb.atuacidade.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfil(navController: NavController, modifier: Modifier = Modifier) {
    val usuarioLogado = SessaoUsuario.usuarioLogado

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Foto do perfil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Nome: ${usuarioLogado?.nome}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "CPF: ${usuarioLogado?.cpf}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Data de Nascimento: ${usuarioLogado?.dataNascimento}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Username: @${usuarioLogado?.username}", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    SessaoUsuario.usuarioLogado = null
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4BABBE),
                    contentColor = Color.White
                ),
            ) {
                Text("Sair")
            }
        }
    }
}
