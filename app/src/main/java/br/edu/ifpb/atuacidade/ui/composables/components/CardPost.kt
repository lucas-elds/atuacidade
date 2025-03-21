package br.edu.ifpb.atuacidade.ui.composables.components

import android.widget.Toast
import br.edu.ifpb.atuacidade.util.usuarioApoiou
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpb.atuacidade.data.model.Post
import br.edu.ifpb.atuacidade.service.PostsDAO
import br.edu.ifpb.atuacidade.ui.composables.screens.SessaoUsuario
import br.edu.ifpb.atuacidade.util.apoiar
import br.edu.ifpb.atuacidade.util.retirarApoio
import br.edu.ifpb.atuacidade.util.nomeAutor
import br.edu.ifpb.atuacidade.util.qntApoios
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import br.edu.ifpb.atuacidade.util.fetchAddress
import br.edu.ifpb.atuacidade.util.postsDAO

@Composable
fun CardPost(post: Post, onPostDeleted: () -> Unit) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var apoios = remember { mutableIntStateOf(0) }
    var nickname = remember { mutableStateOf("") }
    var endereco = remember { mutableStateOf("Carregando...") }
    var postApoiado = remember { mutableStateOf(false) }

    val usuarioLogadoId = SessaoUsuario.usuarioLogado?.id

    LaunchedEffect(Unit) {
        nomeAutor(post.autorId) { resultado -> nickname.value = resultado }
        usuarioApoiou(post.id!!, usuarioLogadoId!!) { resultado -> postApoiado.value = resultado }
        qntApoios(post.id!!) { resultado -> apoios.intValue = resultado.size }
        fetchAddress(post.localizacao.latitude, post.localizacao.longitude) { resultado ->
            endereco.value = resultado?.let {
                "Rua: ${it.road} | Bairro: ${it.suburb} | Cidade: ${it.city}"
            } ?: "Endereço não encontrado"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "@${nickname.value}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = post.categoria,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(2.dp, Color.DarkGray)
                        .background(Color.DarkGray)
                ) {
                    post.midia?.let {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = "Imagem do post",
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = post.descricao,
                    fontSize = 16.sp,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ícone de Local",
                        modifier = Modifier.size(25.dp)
                    )
                    Text(
                        text = endereco.value,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.Start)
                    )
                }
            }

            // Botão More Vert (aparece apenas se o usuário logado for o autor do post)
            if (usuarioLogadoId == post.autorId) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Mais opções"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Excluir") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Ícone de Lixeira",
                                    tint = Color.Red
                                )
                            },
                            onClick = {
                                expanded = false
                                postsDAO.excluir(post.id!!) {
                                    if (it) {
                                        Toast.makeText(
                                            context,
                                            "Postagem excluída!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onPostDeleted()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Erro ao excluir postagem",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Marcar como Concluído") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Ícone de Check",
                                    tint = Color.Green
                                )
                            },
                            onClick = {
                                expanded = false
                                val postAtualizado = post.copy(status = "Concluído")
                                postsDAO.atualizar(postAtualizado) { sucesso ->
                                    if (sucesso) {
                                        Toast.makeText(
                                            context,
                                            "Postagem marcada como concluída!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onPostDeleted()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Erro ao atualizar postagem",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        )
                    }

                }
            }
        }
    }
}

