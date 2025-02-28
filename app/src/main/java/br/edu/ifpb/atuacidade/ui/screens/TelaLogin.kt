package br.edu.ifpb.atuacidade.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.edu.ifpb.atuacidade.model.Usuario
import br.edu.ifpb.atuacidade.model.service.UsuarioDAO

@Composable
fun TelaLogin(navController: NavController, modifier: Modifier) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    val usuarioDAO = UsuarioDAO()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Entre na sua conta",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 25.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nome de usuário") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                maxLines = 1,
                singleLine = true
            )

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                maxLines = 1,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (username.isNotBlank() && senha.isNotBlank()) {
                        usuarioDAO.buscarPorUsername(username) { usuario ->
                            if (usuario != null && usuario.senha == senha) {
                                SessaoUsuario.usuarioLogado = usuario
                                navController.navigate("principal")
                                Toast.makeText(context, "Login bem-sucedido!", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Usuário ou senha incorretos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Entrar")
            }

            TextButton(
                onClick = { navController.navigate("cadastro") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Não tem uma conta? Cadastre-se agora.") }
        }
    }
}

object SessaoUsuario {
    var usuarioLogado: Usuario? = null
}
