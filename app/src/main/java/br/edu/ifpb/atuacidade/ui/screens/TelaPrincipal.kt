package br.edu.ifpb.atuacidade.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun TelaPrincipal(navController: NavController, modifier: Modifier) {
    Text(text = "Bem-vindo à Tela Principal!")
}
