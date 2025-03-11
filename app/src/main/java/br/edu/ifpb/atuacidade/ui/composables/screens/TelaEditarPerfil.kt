package br.edu.ifpb.atuacidade.ui.composables.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.edu.ifpb.atuacidade.service.UsuarioDAO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEditarDados(navController: NavController) {
    val usuarioLogado = SessaoUsuario.usuarioLogado ?: return
    val context = LocalContext.current
    val usuarioDAO = UsuarioDAO()

    val nome = remember { mutableStateOf(usuarioLogado.nome) }
    val username = remember { mutableStateOf(usuarioLogado.username) }
    val dataNascimento = remember { mutableStateOf(usuarioLogado.dataNascimento) }
    val erroUsername = remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nome.value,
                onValueChange = { nome.value = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text("Nome de Usuário") },
                isError = erroUsername.value != null,
                modifier = Modifier.fillMaxWidth()
            )
            erroUsername.value?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = dataNascimento.value,
                onValueChange = { dataNascimento.value = it },
                label = { Text("Data de Nascimento") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    usuarioDAO.buscarPorUsername(username.value) { usuarioExistente ->
                        if (usuarioExistente != null && usuarioExistente.id != usuarioLogado.id) {
                            erroUsername.value = "Nome de usuário já cadastrado"
                        } else {
                            usuarioDAO.atualizarUsuario(
                                usuarioLogado.copy(
                                    nome = nome.value,
                                    username = username.value,
                                    dataNascimento = dataNascimento.value
                                )
                            )
                            {
                                usuarioDAO.buscarPorId(usuarioLogado.id) {
                                    usuarioBuscado ->  SessaoUsuario.usuarioLogado = usuarioBuscado
                                }

                                Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                                navController.navigate("principal")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4BABBE))
            ) {
                Text("Salvar")
            }
        }
    }
}