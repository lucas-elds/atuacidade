package br.edu.ifpb.atuacidade.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.edu.ifpb.atuacidade.R

@Composable
fun TelaHome(navController: NavController, modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.atuacidade_banner),
            contentDescription = "Logomarca do aplicativo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4BABBE),
                        contentColor = Color(0xFFFFFFFF)
                    ),
                ) {
                    Text("Iniciar")
                }

            }
        }
    }
}