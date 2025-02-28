package br.edu.ifpb.atuacidade.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.edu.ifpb.atuacidade.model.Post
import br.edu.ifpb.atuacidade.model.service.DownvoteDAO
import br.edu.ifpb.atuacidade.model.service.PostsDAO
import br.edu.ifpb.atuacidade.model.service.UpvoteDAO
import br.edu.ifpb.atuacidade.ui.theme.AtuacidadeTheme

@Composable
fun TelaPrincipal(navController: NavController, modifier: Modifier = Modifier) {
    val postsDAO = PostsDAO()
    var listaPosts by remember { mutableStateOf<List<Post>>(emptyList()) }

    LaunchedEffect(Unit) {
        postsDAO.buscar { posts -> listaPosts = posts }
    }

    AtuacidadeTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                BarraFixaTopo(navController)

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(listaPosts) { post ->
                        CardPost(post, UpvoteDAO(), DownvoteDAO(), PostsDAO())
                    }
                }
            }

            FloatingActionButton(
                onClick = {  },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 15.dp, bottom = 70.dp)
                    .size(70.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(100.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate("postagens") }
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
