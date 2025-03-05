@file:OptIn(ExperimentalPermissionsApi::class)
package br.edu.ifpb.atuacidade.ui.composables.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.atuacidade.service.PostagemViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TelaPostagens(
    viewModel: PostagemViewModel = viewModel(),
    onVoltar: () -> Unit,
    onSucesso: () -> Unit
) {
    val context = LocalContext.current
    val estado by viewModel.uiState.collectAsState()

    val locationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Manifest.permission.ACCESS_FINE_LOCATION
    } else {
        Manifest.permission.ACCESS_COARSE_LOCATION
    }

    val permissionState = rememberPermissionState(locationPermission)

    LaunchedEffect(estado.sucesso) {
        if (estado.sucesso) onSucesso()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Reclamação") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { CampoDescricao(viewModel) }
                item { CampoURL(viewModel) }
                item { SeletorCategoria(viewModel) }
                item { BotaoLocalizacao(permissionState) { viewModel.obterLocalizacao(context) } }
                item {
                    estado.erro?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
                item { BotaoEnviar(estado.carregando) { viewModel.enviarPostagem() } }
            }
        }
    }
}


@Composable
private fun CampoDescricao(viewModel: PostagemViewModel) {
    val estado by viewModel.uiState.collectAsState()
    OutlinedTextField(
        value = estado.descricao,
        onValueChange = viewModel::atualizarDescricao,
        label = { Text("Descrição") },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        maxLines = 5
    )
}

@Composable
private fun CampoURL(viewModel: PostagemViewModel) {
    val estado by viewModel.uiState.collectAsState()
    OutlinedTextField(
        value = estado.url,
        onValueChange = viewModel::atualizarURL,
        label = { Text("URL da Imagem") },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        maxLines = 1
    )
}

@Composable
private fun SeletorCategoria(viewModel: PostagemViewModel) {
    val estado by viewModel.uiState.collectAsState()
    val categorias = listOf(
        "Vazamento", "Buraco na Rua", "Iluminação",
        "Terreno Baldio", "Transporte Público",
        "Área de Risco", "Infraestrutura", "Saúde"
    )

    var expandido by remember { mutableStateOf(false) }

    Column {
        Text(text = "Categoria", style = MaterialTheme.typography.bodyLarge)

        Box {
           OutlinedButton(
                onClick = { expandido = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(estado.categoriaSelecionada.ifEmpty { "Selecione a categoria" })
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria) },
                        onClick = {
                            viewModel.atualizarCategoria(categoria)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BotaoLocalizacao(
    permissionState: PermissionState,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            if (permissionState.status.isGranted) {
                onClick()
            } else {
                permissionState.launchPermissionRequest()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3ea52e))
    ) {
        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Localização")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Obter localização atual")
    }
}

@Composable
private fun BotaoEnviar(carregando: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !carregando
    ) {
        if (carregando) {
            CircularProgressIndicator()
        } else {
            Text("Publicar")
        }
    }
}
