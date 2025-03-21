package br.edu.ifpb.atuacidade.ui.composables.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpb.atuacidade.data.model.Post
import br.edu.ifpb.atuacidade.ui.composables.screens.SessaoUsuario
import br.edu.ifpb.atuacidade.util.fetchAddress
import br.edu.ifpb.atuacidade.util.qntApoios
import br.edu.ifpb.atuacidade.util.usernameAutor
import br.edu.ifpb.atuacidade.util.usuarioApoiou
import coil.compose.rememberAsyncImagePainter
import br.edu.ifpb.atuacidade.data.model.Comentario
import br.edu.ifpb.atuacidade.ui.theme.DarkBlue
import br.edu.ifpb.atuacidade.ui.theme.MidBlue
import br.edu.ifpb.atuacidade.util.comentar
import br.edu.ifpb.atuacidade.util.comentariosPorPost

@Composable
fun CardCompleto(post: Post) {
    var comentario = remember { mutableStateOf("") }

    var listaComentarios by remember { mutableStateOf<List<Comentario>>(emptyList()) }
    var apoios = remember { mutableIntStateOf(0) }
    var nickname = remember { mutableStateOf("") }
    var endereco = remember { mutableStateOf("Carregando...") }
    var postApoiado = remember { mutableStateOf(false) }
    val usuarioLogadoId = SessaoUsuario.usuarioLogado?.id

    LaunchedEffect(Unit) {
        usernameAutor(post.autorId) { resultado -> nickname.value = resultado }
        usuarioApoiou(post.id!!, usuarioLogadoId!!) { resultado -> postApoiado.value = resultado }
        qntApoios(post.id!!) { resultado -> apoios.intValue = resultado.size }
        comentariosPorPost(post.id!!) { comentarios -> listaComentarios = comentarios }
        fetchAddress(post.localizacao.latitude, post.localizacao.longitude) { resultado ->
            if (resultado != null) {
                endereco.value = "Rua: ${resultado.road} | Bairro: ${resultado.suburb} | Cidade: ${resultado.city}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(20.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .windowInsetsPadding(WindowInsets.ime)
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "@${nickname.value}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        post.midia?.let {
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = "Imagem do post",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = post.descricao,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 10.dp).fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ícone de Local",
                            modifier = Modifier.size(25.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Endereço:", fontSize = 16.sp, textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = endereco.value,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp), thickness = 1.dp, color = Color.Gray)
                    Text(
                        text = "Total de apoios: ${apoios.intValue}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (listaComentarios.isEmpty()) {
                        Text("Ainda não há comentários.", fontSize = 16.sp, textAlign = TextAlign.Center, color = Color.Gray)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 250.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            items(listaComentarios) { comentario ->
                                ComentarioItem(comentario)
                            }
                        }
                    }
                }
            }
            BottomAppBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                containerColor = DarkBlue
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = comentario.value,
                        onValueChange = { comentario.value = it },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        placeholder = { Text("Digite seu comentário...") },
                        textStyle = TextStyle(color = Color.White),
                        shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp)
                    )
                    IconButton(
                        onClick = {
                            if (comentario.value.isNotBlank()) {
                                comentar(usuarioLogadoId!!, post.id!!, comentario.value) {
                                    comentariosPorPost(post.id!!) { comentarios -> listaComentarios = comentarios }
                                }
                                comentario.value = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Enviar comentário",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }

}
