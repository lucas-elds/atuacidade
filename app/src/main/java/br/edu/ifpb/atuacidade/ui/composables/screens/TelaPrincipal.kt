package br.edu.ifpb.atuacidade.ui.composables.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.edu.ifpb.atuacidade.data.model.Post
import br.edu.ifpb.atuacidade.service.PostsDAO
import br.edu.ifpb.atuacidade.ui.composables.components.BarraFixaTopo
import br.edu.ifpb.atuacidade.ui.composables.components.CardCompleto
import br.edu.ifpb.atuacidade.ui.composables.components.CardPost
import br.edu.ifpb.atuacidade.ui.theme.AtuacidadeTheme
import br.edu.ifpb.atuacidade.ui.theme.Horizon
import br.edu.ifpb.atuacidade.util.comentar
import br.edu.ifpb.atuacidade.util.comentariosPorPost

@Composable
fun TelaPrincipal(navController: NavController) {
    val postsDAO = PostsDAO()
    var listaPosts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var postSelecionado by remember { mutableStateOf<Post?>(null) }

    LaunchedEffect(Unit) {
        postsDAO.buscar { posts ->
            listaPosts = posts.filter { it.status == "Pendente" }
        }
    }

    AtuacidadeTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BarraFixaTopo(navController)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "PRINCIPAIS POSTAGENS",
                    style = TextStyle(
                        fontFamily = Horizon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        postsDAO.buscar { posts ->
                            listaPosts = posts.filter { it.status == "Pendente" }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Atualizar",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Atualizar",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray
                )

                if(postSelecionado == null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 40.dp)
                    ) {
                        items(listaPosts) { post ->
                            CardPost(
                                post = post,
                                onClick = { postSelecionado = post },
                                onPostDeleted = {
                                    listaPosts = listaPosts.filter { it.id != post.id }
                                }
                            )
                        }
                    }
                } else {
                    CardCompleto(postSelecionado!!)
                }
            }

            if(postSelecionado == null){
                FloatingActionButton(
                    onClick = { navController.navigate("postagens") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 15.dp, bottom = 70.dp)
                        .size(70.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
