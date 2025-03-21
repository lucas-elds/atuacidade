@file:OptIn(ExperimentalPermissionsApi::class)
package br.edu.ifpb.atuacidade.ui.composables.screens

import PostagemUtil
import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.atuacidade.ui.theme.MidBlue
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

    LaunchedEffect(Unit) {
        viewModel.carregarCategorias()
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
                item { BotaoLocalizacao(estado.carregando, permissionState) { viewModel.obterLocalizacao(context) } }
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
private fun SeletorCategoria(viewModel: PostagemUtil) {
    val estado by viewModel.uiState.collectAsState()
    var expandido by remember { mutableStateOf(false) }

    Column {
        Text(text = "Categoria", style = MaterialTheme.typography.bodyLarge)

        Box {
            OutlinedButton(
                onClick = { expandido = true },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                shape = RectangleShape
            ) {
                Text(estado.categoriaSelecionada.ifEmpty { "Selecione a categoria" })
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                estado.categorias.forEach { categoria ->
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
    carregando: Boolean,
    permissionState: PermissionState,
    onClick: () -> Unit
) {
    val estado by rememberUpdatedState(newValue = permissionState.status.isGranted)
    val uiState by viewModel<PostagemUtil>().uiState.collectAsState()

    Button(
        onClick = {
            if (estado) {
                onClick()
            } else {
                permissionState.launchPermissionRequest()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (uiState.localizacaoObtida) Color(0xFF4CAF50) else MidBlue
        )
    ) {
        if (carregando) {
            CircularProgressIndicator()
        } else {
            Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Localização")
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (uiState.localizacaoObtida) "Localização obtida" else "Obter localização atual")
        }
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
