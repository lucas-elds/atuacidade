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
) {}