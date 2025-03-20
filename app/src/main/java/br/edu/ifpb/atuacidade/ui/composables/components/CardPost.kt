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
import androidx.compose.material.icons.filled.Close
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
import br.edu.ifpb.atuacidade.ui.composables.screens.SessaoUsuario
import br.edu.ifpb.atuacidade.util.apoiar
import br.edu.ifpb.atuacidade.util.retirarApoio
import br.edu.ifpb.atuacidade.util.nomeAutor
import br.edu.ifpb.atuacidade.util.qntApoios
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun CardPost(post: Post) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var apoios = remember { mutableIntStateOf(0) }
    var nickname = remember { mutableStateOf("") }
    var postApoiado = remember { mutableStateOf(false) }

    val usuarioLogadoId = SessaoUsuario.usuarioLogado?.id

    LaunchedEffect(Unit) {
        nomeAutor(post.autorId) { resultado -> nickname.value = resultado }
        usuarioApoiou(post.id!!, usuarioLogadoId!!) { resultado -> postApoiado.value = resultado }
        qntApoios(post.id!!){ resultado -> apoios.intValue = resultado.size }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
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
            ){
                post.midia?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Imagem do post",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.descricao,
                fontSize = 16.sp,
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 15.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Total de apoios: ${apoios.intValue}",
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        if(postApoiado.value){
                            retirarApoio(post, usuarioLogadoId!!) {
                                coroutineScope.launch{
                                    usuarioApoiou(post.id!!, usuarioLogadoId!!) { resultado -> postApoiado.value = resultado }
                                    qntApoios(post.id!!){ resultado -> apoios.intValue = resultado.size }
                                }

                                Toast.makeText(
                                    context,
                                    "Você deixou de apoiar",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            apoiar(post, usuarioLogadoId!!){
                                coroutineScope.launch{
                                    usuarioApoiou(post.id!!, usuarioLogadoId!!) { resultado -> postApoiado.value = resultado }
                                    qntApoios(post.id!!){ resultado -> apoios.intValue = resultado.size }
                                }

                                Toast.makeText(
                                    context,
                                    "Você apoiou!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (postApoiado.value) Color(0xFFAD1212) else Color(0xFF3ea52e)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (postApoiado.value) Icons.Filled.Close else Icons.Filled.Check,
                        contentDescription = "Apoio",
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (postApoiado.value) "Retirar apoio" else "Apoiar",
                        color = Color.White
                    )
                }
            }
        }
    }
}
