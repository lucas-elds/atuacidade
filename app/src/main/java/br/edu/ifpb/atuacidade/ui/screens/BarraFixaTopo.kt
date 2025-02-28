package br.edu.ifpb.atuacidade.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.edu.ifpb.atuacidade.ui.theme.Horizon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraFixaTopo(navController: NavController) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxHeight()
                    .padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ATUACIDADE",
                    style = TextStyle(
                        fontFamily = Horizon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                )
            }
        },
        actions = {
            Row(
                modifier = Modifier.fillMaxHeight()
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigate("perfil") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Perfil",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.height(120.dp)
    )
}

