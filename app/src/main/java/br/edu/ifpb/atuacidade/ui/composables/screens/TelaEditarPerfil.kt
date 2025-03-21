package br.edu.ifpb.atuacidade.ui.composables.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.edu.ifpb.atuacidade.data.model.Usuario
import br.edu.ifpb.atuacidade.service.UsuarioDAO
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEditarPerfil(navController: NavController) {
    val usuarioLogado = SessaoUsuario.usuarioLogado ?: return
    val context = LocalContext.current
    val usuarioDAO = UsuarioDAO()

    val nome = remember { mutableStateOf(usuarioLogado.nome) }
    val username = remember { mutableStateOf(usuarioLogado.username) }
    val dataNascimento = remember { mutableStateOf(usuarioLogado.dataNascimento) }
    val erroUsername = remember { mutableStateOf<String?>(null) }

    val imagemUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagemUri.value = uri
    }

    val isSaving = remember { mutableStateOf(false) }

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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            ) {
                if (imagemUri.value != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imagemUri.value),
                        contentDescription = "Imagem de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4BABBE))
            ) {
                Icon(imageVector = Icons.Filled.Image, contentDescription = "Selecionar Imagem")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Selecionar Imagem")
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    isSaving.value = true

                    usuarioDAO.buscarPorUsername(username.value) { usuarioExistente ->
                        if (usuarioExistente != null && usuarioExistente.id != usuarioLogado.id) {
                            erroUsername.value = "Nome de usuário já cadastrado"
                            isSaving.value = false
                        } else {
                            if (imagemUri.value != null) {
                                uploadImagemParaFirebase(imagemUri.value!!) { urlImagem ->
                                    salvarUsuario(usuarioDAO, usuarioLogado, nome.value, username.value, dataNascimento.value, urlImagem, context, navController, isSaving)
                                }
                            } else {
                                salvarUsuario(usuarioDAO, usuarioLogado, nome.value, username.value, dataNascimento.value, usuarioLogado.fotoPerfil, context, navController, isSaving)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4BABBE)),
                enabled = !isSaving.value
            ) {
                if (isSaving.value) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Salvar")
                }
            }
        }
    }
}

private fun uploadImagemParaFirebase(uri: Uri, onSuccess: (String) -> Unit) {
    val usuarioAuth = SessaoUsuario.usuarioLogado
    val storageRef = Firebase.storage.reference.child("${usuarioAuth!!.cpf}/${uri.lastPathSegment}")

    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uriDownload ->
                onSuccess(uriDownload.toString())
            }
        }
}

private fun salvarUsuario(
    usuarioDAO: UsuarioDAO,
    usuarioLogado: Usuario,
    nome: String,
    username: String,
    dataNascimento: String,
    fotoPerfilUrl: String?,
    context: Context,
    navController: NavController,
    isSaving: MutableState<Boolean>
) {
    usuarioDAO.atualizarUsuario(
        usuarioLogado.copy(
            nome = nome,
            username = username,
            dataNascimento = dataNascimento,
            fotoPerfil = fotoPerfilUrl
        )
    ) {
        usuarioDAO.buscarPorId(usuarioLogado.id) { usuarioBuscado ->
            SessaoUsuario.usuarioLogado = usuarioBuscado
        }

        Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
        isSaving.value = false
        navController.navigate("principal")
    }
}
