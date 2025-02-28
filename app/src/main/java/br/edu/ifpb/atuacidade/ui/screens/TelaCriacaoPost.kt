package br.edu.ifpb.atuacidade.ui.screens

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.ifpb.atuacidade.model.Localizacao
import br.edu.ifpb.atuacidade.model.Post
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun TelaCriacaoPost(
    onPostCreated: (Post) -> Unit, // Callback para quando o post for criado
    modifier: Modifier
) {
    // Estado para os campos do formulário
    var descricao by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf("") }
    var midiaUri by remember { mutableStateOf<Uri?>(null) }

    // Estado para a localização do usuário
    var localizacao by remember {
        mutableStateOf(Localizacao(0.0, 0.0)) // Inicializa com valores padrão
    }

    // Launcher para seleção de mídia (foto ou vídeo)
    val launcherMidia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        midiaUri = uri
    }

    // Função para capturar a localização do usuário (simplificado)
    LaunchedEffect(Unit) {
        // Aqui você pode integrar com a API de localização do Android
        // para capturar a latitude e longitude do usuário.
        localizacao = Localizacao(-7.12345, -34.98765) // Exemplo de coordenadas
    }

    // Função para criar o post
    @RequiresApi(Build.VERSION_CODES.O)
    fun criarPost() {
        val autorId = "Kkk"
        val dataHora = "sim"
        val midia = midiaUri?.let { uri ->
            // Converter a URI da mídia para ByteArray (depende da implementação)
            // Exemplo simplificado:
            // contentResolver.openInputStream(uri)?.readBytes()
            byteArrayOf() // Substitua pela lógica real
        } ?: byteArrayOf()

        val novoPost = Post(
            id = "", // O Firestore pode gerar o ID automaticamente
            autorId = autorId,
            dataHora = dataHora,
            midia = midia,
            descricao = descricao,
            status = "Pendente",
            upvotes = 0,
            downvotes = 0,
            categoria = categoriaSelecionada,
            localizacao = localizacao
        )

        onPostCreated(novoPost) // Chama o callback com o novo post
    }

    // Layout da tela
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Criar Nova Postagem",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de descrição
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Seleção de categoria
            val categorias = listOf(
                "Vazamento",
                "Buraco na Rua",
                "Iluminação",
                "Terreno Baldio",
                "Transporte público",
                "Área de Risco",
                "Infraestrutura",
                "Atendimento de Saúde"
            )
            DropdownMenu(
                expanded = false, // Controle de visibilidade do menu
                onDismissRequest = { /* Fechar menu */ }
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        onClick = {
                            categoriaSelecionada = categoria
                        },
                        text = { Text(categoria) }
                    )
                }
            }
            OutlinedButton(
                onClick = { /* Abrir menu de categorias */ }
            ) {
                Text(categoriaSelecionada.ifEmpty { "Selecione uma categoria" })
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Upload de mídia
            Button(
                onClick = { launcherMidia.launch("image/* video/*") }
            ) {
                Text("Adicionar Mídia (Foto/Video)")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botão para criar o post
            Button(
                onClick = { criarPost() },
                modifier = Modifier.fillMaxWidth(),
                enabled = descricao.isNotEmpty() && categoriaSelecionada.isNotEmpty()
            ) {
                Text("Criar Postagem")
            }
        }
    }
}