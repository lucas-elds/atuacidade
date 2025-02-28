package br.edu.ifpb.atuacidade.ui.screens

import br.edu.ifpb.atuacidade.ui.screens.SessaoUsuario
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color

@Composable
fun TelaPerfil(navController: NavController, modifier: Modifier = Modifier) {

    var usuarioLogado = SessaoUsuario.usuarioLogado

    Column(
        modifier = modifier
            .fillMaxSize()
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
    }
}
