@file:OptIn(ExperimentalPermissionsApi::class)
package br.edu.ifpb.atuacidade.ui.composables.screens

import PostagemUtil
import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TelaPostagens(
    viewModel: PostagemUtil = viewModel(),
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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
                item { BotaoSelecionarImagem(viewModel) }
                item { CampoDescricao(viewModel) }
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
private fun CampoDescricao(viewModel: PostagemUtil) {
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
private fun CampoURL(viewModel: PostagemUtil) {
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
private fun SeletorCategoria(viewModel: PostagemUtil) {
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
private fun BotaoSelecionarImagem(viewModel: PostagemUtil) {
    val context = LocalContext.current
    val estado by viewModel.uiState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.atualizarImagemSelecionada(it)
        }
    }

    Column {
        estado.imagemSelecionada?.let { uri ->
            Image(
                bitmap = rememberImageBitmap(uri, context),
                contentDescription = "Prévia da Imagem",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp))
        }

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = "Selecionar Imagem",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Selecionar Imagem",
                    color = Color.White
                )
            }
        }


    }
}

@Composable
private fun rememberImageBitmap(uri: Uri, context: Context): ImageBitmap {
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        bitmap.value = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
    }
    return bitmap.value?.asImageBitmap() ?: ImageBitmap(1, 1)
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
