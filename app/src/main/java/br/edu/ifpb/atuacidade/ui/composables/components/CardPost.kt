package br.edu.ifpb.atuacidade.ui.composables.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpb.atuacidade.model.Downvote
import br.edu.ifpb.atuacidade.model.Post
import br.edu.ifpb.atuacidade.model.Upvote
import br.edu.ifpb.atuacidade.model.service.UsuarioDAO
import br.edu.ifpb.atuacidade.model.service.UpvoteDAO
import br.edu.ifpb.atuacidade.model.service.DownvoteDAO
import br.edu.ifpb.atuacidade.model.service.PostsDAO
import br.edu.ifpb.atuacidade.ui.composables.screens.SessaoUsuario
import coil.compose.rememberAsyncImagePainter

@Composable
fun CardPost(post: Post, upvoteDAO: UpvoteDAO, downvoteDAO: DownvoteDAO, postDAO: PostsDAO) {
    var autorNome by remember { mutableStateOf("Carregando...") }
    val upvotes = remember { mutableStateOf(0) }
    val downvotes = remember { mutableStateOf(0) }
    val usuarioUpvoted = remember { mutableStateOf(false) }
    val usuarioDownvoted = remember { mutableStateOf(false) }
    val botaoHabilitado = remember { mutableStateOf(true) }

    val usuarioDAO = UsuarioDAO()
    val usuarioLogadoId = SessaoUsuario.usuarioLogado?.id

    LaunchedEffect(post.id) {
        usuarioDAO.buscarPorId(post.autorId) { usuario ->
            autorNome = usuario?.nome ?: "Desconhecido"
        }
        upvoteDAO.buscarPorPostId(post.id!!) { upvoteList ->
            upvotes.value = upvoteList.size
            usuarioUpvoted.value = upvoteList.any { it.usuarioId == usuarioLogadoId }
        }
        downvoteDAO.buscarPorPostId(post.id!!) { downvoteList ->
            downvotes.value = downvoteList.size
            usuarioDownvoted.value = downvoteList.any { it.usuarioId == usuarioLogadoId }
        }
    }

    fun realizarUpvote() {
        if (usuarioLogadoId != null) {
            if (usuarioUpvoted.value) {
                upvoteDAO.buscarPorPostId(post.id!!) { upvoteList ->
                    val upvote = upvoteList.find { it.usuarioId == usuarioLogadoId }
                    if (upvote != null) {
                        upvoteDAO.deletar(upvote.id) { sucesso ->
                            if (sucesso) {
                                upvotes.value--
                                usuarioUpvoted.value = false
                            }
                        }
                    }
                }
            } else {
                val novoUpvote = Upvote(id = "", postId = post.id!!, usuarioId = usuarioLogadoId)
                upvoteDAO.adicionar(novoUpvote) { upvote ->
                    if (upvote != null) {
                        upvotes.value++
                        usuarioUpvoted.value = true

                        if (usuarioDownvoted.value) {
                            downvoteDAO.buscarPorPostId(post.id!!) { downvoteList ->
                                val downvote = downvoteList.find { it.usuarioId == usuarioLogadoId }
                                if (downvote != null) {
                                    downvoteDAO.deletar(downvote.id) { sucesso ->
                                        if (sucesso) {
                                            downvotes.value--
                                            usuarioDownvoted.value = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun realizarDownvote() {
        if (usuarioLogadoId != null) {
            if (usuarioDownvoted.value) {
                downvoteDAO.buscarPorPostId(post.id!!) { downvoteList ->
                    val downvote = downvoteList.find { it.usuarioId == usuarioLogadoId }
                    if (downvote != null) {
                        downvoteDAO.deletar(downvote.id) { sucesso ->
                            if (sucesso) {
                                downvotes.value--
                                usuarioDownvoted.value = false
                            }
                        }
                    }
                }
            } else {
                val novoDownvote = Downvote(id = "", postId = post.id!!, usuarioId = usuarioLogadoId)
                downvoteDAO.adicionar(novoDownvote) { downvote ->
                    if (downvote != null) {
                        downvotes.value++
                        usuarioDownvoted.value = true

                        if (usuarioUpvoted.value) {
                            upvoteDAO.buscarPorPostId(post.id!!) { upvoteList ->
                                val upvote = upvoteList.find { it.usuarioId == usuarioLogadoId }
                                if (upvote != null) {
                                    upvoteDAO.deletar(upvote.id) { sucesso ->
                                        if (sucesso) {
                                            upvotes.value--
                                            usuarioUpvoted.value = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = post.descricao,
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

            Spacer(modifier = Modifier.height(8.dp))

            post.midia?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagem do post",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Autor: $autorNome",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { realizarUpvote() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3ea52e)),
                    modifier = Modifier.weight(0.5f),
                    enabled = botaoHabilitado.value
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Upvote",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${upvotes.value}", color = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { realizarDownvote() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff3131)),
                    modifier = Modifier.weight(0.5f),
                    enabled = botaoHabilitado.value
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Downvote",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${downvotes.value}", color = Color.White)
                }
            }
        }
    }
}
