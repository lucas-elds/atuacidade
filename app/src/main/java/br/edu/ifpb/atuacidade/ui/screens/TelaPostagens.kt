@file:OptIn(ExperimentalPermissionsApi::class)
package br.edu.ifpb.atuacidade.ui.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TelaPostagens(
    viewModel: PostagemViewModel = viewModel(),
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            CampoDescricao(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            CampoURL(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            SeletorCategoria(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            BotaoLocalizacao(permissionState) {
                viewModel.obterLocalizacao(context)
            }
            Spacer(modifier = Modifier.height(16.dp))

            estado.latitude?.let { lat ->
                estado.longitude?.let { long ->
                    Text("Localização: $lat, $long")
                    Log.d("ATUACIDADE", "Mensagem: $lat, $long")
                }
            }

            estado.erro?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
            BotaoEnviar(estado.carregando) {
                viewModel.enviarPostagem()
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
        modifier = Modifier.fillMaxWidth(),
        maxLines = 5
    )
}

@Composable
private fun CampoURL(viewModel: PostagemViewModel) {
    val estado by viewModel.uiState.collectAsState()
    OutlinedTextField(
        value = estado.url,
        onValueChange = viewModel::atualizarDescricao,
        label = { Text("URL da Imagem") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1
    )
}

@Composable
private fun SeletorImagem(
    imagePicker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    uri: Uri?
) {
    Column {
        Button(onClick = {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text("Selecionar Imagem")
        }

        uri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Imagem selecionada",
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun SeletorCategoria(viewModel: PostagemViewModel) {
    val estado by viewModel.uiState.collectAsState()
    val categorias = listOf(
        "Vazamento", "Buraco na Rua", "Iluminação",
        "Terreno Baldio", "Transporte público",
        "Área de Risco", "Infraestrutura", "Saúde"
    )

    var expandido by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expandido = true }) {
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

@Composable
private fun BotaoLocalizacao(
    permissionState: PermissionState,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Button(onClick = {
        if (permissionState.status.isGranted) {
            onClick()
        } else {
            permissionState.launchPermissionRequest()
        }
    }) {
        Text("Obter Localização Atual")
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
            Text("Publicar Postagem")
        }
    }
}