package br.edu.ifpb.atuacidade.ui.composables.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpb.atuacidade.data.model.Comentario
import br.edu.ifpb.atuacidade.ui.theme.DarkBlue
import br.edu.ifpb.atuacidade.util.usernameAutor
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ComentarioItem(comentario: Comentario) {

    val nickname = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        usernameAutor(comentario.autorId) { resultado -> nickname.value = resultado }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "@${nickname.value}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DarkBlue
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comentario.texto,
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enviado em: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(comentario.dataHora.toDate())}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
