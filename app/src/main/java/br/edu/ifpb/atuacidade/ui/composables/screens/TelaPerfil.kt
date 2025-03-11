package br.edu.ifpb.atuacidade.ui.composables.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.edu.ifpb.atuacidade.service.PostsDAO
import br.edu.ifpb.atuacidade.service.UsuarioDAO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfil(navController: NavController) {
    val usuarioLogado = SessaoUsuario.usuarioLogado
    val showDialog = remember { mutableStateOf(false) }
    val menuExpanded = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded.value = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Mais opções")
                    }
                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar Dados") },
                            onClick = {
                                menuExpanded.value = false
                                navController.navigate("editar_dados")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Excluir Conta") },
                            onClick = {
                                menuExpanded.value = false
                                showDialog.value = true
                            }
                        )
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
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Foto do perfil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = usuarioLogado?.nome ?: "Carregando...",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "@${usuarioLogado?.username ?: "username"}",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair")
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Apagar Conta") },
            text = { Text("Tem certeza que deseja apagar sua conta? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        deletarUsuarioEPosts(usuarioLogado?.id ?: "", context) {
                            SessaoUsuario.usuarioLogado = null
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Sim")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Não")
                }
            }
        )
    }
}


private fun deletarUsuarioEPosts(usuarioId: String, context: android.content.Context, onComplete: () -> Unit) {
    val usuarioDAO = UsuarioDAO()
    val postsDAO = PostsDAO()

    postsDAO.buscarPorAutorId(usuarioId) { posts ->
        posts.forEach { post ->
            postsDAO.deletar(post.id ?: "") { success ->
                if (!success) {
                    Toast.makeText(context, "Erro ao deletar postagem: ${post.id}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        usuarioDAO.deletarUsuario(usuarioId) { success ->
            if (success) {
                Toast.makeText(context, "Conta e postagens deletadas com sucesso!", Toast.LENGTH_SHORT).show()
                onComplete()
            } else {
                Toast.makeText(context, "Erro ao deletar a conta", Toast.LENGTH_SHORT).show()
            }
        }
    }
}