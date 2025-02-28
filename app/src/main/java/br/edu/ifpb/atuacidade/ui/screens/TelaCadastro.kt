package br.edu.ifpb.atuacidade.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavController
import br.edu.ifpb.atuacidade.model.Usuario
import br.edu.ifpb.atuacidade.model.Endereco
import br.edu.ifpb.atuacidade.model.service.UsuarioDAO
import br.edu.ifpb.atuacidade.util.buscarUsername
import br.edu.ifpb.atuacidade.util.validarSenha
import br.edu.ifpb.atuacidade.util.validarCPF
import br.edu.ifpb.atuacidade.util.validarDataNascimento
import br.edu.ifpb.atuacidade.util.buscarCep
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun TelaCadastro(navController: NavController, modifier: Modifier) {
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    //campos endereco
    var cep by remember { mutableStateOf("") }
    var rua by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var bairro by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }

    //campos de erro
    var cpfErro by remember { mutableStateOf<String?>(null) }
    var usernameErro by remember { mutableStateOf<String?>(null) }
    var senhaErro by remember { mutableStateOf<String?>(null) }
    var dataNascimentoErro by remember { mutableStateOf<String?>(null) }
    var cpfCadastradoErro by remember { mutableStateOf<String?>(null) }
    var usernameCadastradoErro by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Cadastre-se", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Ícone de Dados Pesoais",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Dados pessoais:",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start)
                )
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                value = nome,
                onValueChange = {
                    nome = it
                },
                label = { Text("Nome*") },
                shape = RoundedCornerShape(20.dp),
                maxLines = 1,
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(0.5f)) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused) {
                                    if (cpf.length in 1..10 || cpf.length > 11) {
                                        cpfErro = "CPF inválido"
                                    }
                                }
                            },
                        value = cpf,
                        onValueChange = {
                            cpf = it
                            cpfErro = null
                            if (cpf.length == 11) {
                                cpfErro = if (validarCPF(it)) {
                                    null
                                } else {
                                    "CPF inválido"
                                }
                            }

                            if (cpf.length > 11) {
                                cpfErro = "CPF inválido"
                            }

                            if (cpf.isEmpty()) {
                                cpfErro = "Informe um CPF"
                            }
                        },
                        label = { Text("CPF*") },
                        shape = RoundedCornerShape(20.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        isError = cpfErro != null,
                        maxLines = 1,
                        singleLine = true
                    )

                    Spacer(Modifier.height(2.dp))

                    AnimatedVisibility(visible = cpfErro != null){
                        Text(
                            text = cpfErro ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(0.5f)){
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = dataNascimento,
                        onValueChange = { input ->
                            val digits = input.text.filter { it.isDigit() }
                            val formattedText = when {
                                digits.length <= 2 -> digits
                                digits.length <= 4 -> "${digits.substring(0, 2)}/${digits.substring(2)}"
                                digits.length <= 8 -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4)}"
                                else -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
                            }

                            if(formattedText.length == 10){
                                dataNascimentoErro = if(validarDataNascimento(dataNascimento)){
                                    null
                                } else {
                                    "Data de nascimento inválida"
                                }
                            }

                            if(formattedText.isEmpty()){
                                dataNascimentoErro = "Informe uma data de nascimento"
                            }

                            dataNascimento = TextFieldValue(
                                text = formattedText,
                                selection = TextRange(formattedText.length)
                            )
                        },
                        label = { Text("Data de nascimento*") },
                        shape = RoundedCornerShape(20.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        isError = dataNascimentoErro != null,
                        maxLines = 1,
                        singleLine = true
                    )

                    Spacer(Modifier.height(2.dp))

                    AnimatedVisibility(visible = dataNascimentoErro != null){
                        Text(
                            text = dataNascimentoErro ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(0.5f)){
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused) {
                                    if (username.length in 1..5) {
                                        usernameErro = "Informe pelo menos 6 caracteres"
                                    } else {
                                        buscarUsername(username) { disponivel ->
                                            usernameErro = if (disponivel) {
                                                null
                                            } else {
                                                "O username já está em uso"
                                            }
                                        }
                                    }
                                }
                            },
                        value = username,
                        onValueChange = {
                            username = it
                            usernameErro = null
                            if (username.length == 6) {
                                usernameErro = null
                            }
                            if (username.isEmpty()) {
                                usernameErro = "Informe um nome de usuário"
                            }
                        },
                        label = { Text("Nome de usuário*") },
                        shape = RoundedCornerShape(20.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        isError = usernameErro != null,
                        maxLines = 1,
                        singleLine = true
                    )

                    Spacer(Modifier.height(2.dp))

                    AnimatedVisibility(visible = usernameErro != null){
                        Text(
                            text = usernameErro ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(0.5f)) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = senha,
                        onValueChange = {
                            senha = it
                            senhaErro = if(validarSenha(senha)){
                                null
                            } else {
                                "Informe 8 dígitos alfanuméricos"
                            }
                        },
                        label = { Text("Senha*") },
                        shape = RoundedCornerShape(20.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        visualTransformation = PasswordVisualTransformation(),
                        isError = senhaErro != null,
                        maxLines = 1,
                        singleLine = true
                    )

                    Spacer(Modifier.height(2.dp))

                    AnimatedVisibility(visible = senhaErro != null){
                        Text(
                            text = senhaErro ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ícone de Local",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Endereço:",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f),
                    value = cep,
                    onValueChange = {
                        cep = it
                        if (cep.length == 8) {
                            buscarCep(cep) { dadosCep ->
                                rua = dadosCep["rua"] ?: ""
                                bairro = dadosCep["bairro"] ?: ""
                                cidade = dadosCep["cidade"] ?: ""
                                estado = dadosCep["estado"] ?: ""
                            }
                        } else {
                            rua = ""
                            bairro = ""
                            cidade = ""
                            estado = ""
                        }
                    },
                    label = { Text("CEP*") },
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    maxLines = 1,
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f),
                    value = bairro, onValueChange = {},
                    label = { Text("Bairro*") },
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    readOnly = true,
                    maxLines = 1,
                    singleLine = true
                )
            }

            val url = "http://www.buscacep.correios.com.br/"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .offset(y = (-10).dp)
                    .padding(start = 5.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.Start)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)

                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Ícone de Busca",
                    tint = Color.Cyan,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Descubra aqui o seu CEP.",
                    fontSize = 13.sp,
                    color = Color.Cyan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start)
                )
            }

            Row(modifier = Modifier.fillMaxWidth().offset(y = (-20).dp), horizontalArrangement = Arrangement.spacedBy(8.dp)){
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f),
                    value = rua, onValueChange = {},
                    label = { Text("Rua*") },
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    readOnly = true
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f),
                    value = numero, onValueChange = { numero = it },
                    label = { Text("Número") },
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    maxLines = 1,
                    singleLine = true
                )
            }

            Row(modifier = Modifier.fillMaxWidth().offset(y = (-20).dp), horizontalArrangement = Arrangement.spacedBy(8.dp)){
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f),
                    value = cidade, onValueChange = {},
                    label = { Text("Cidade*") },
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    readOnly = true,
                    maxLines = 1,
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f),
                    value = estado, onValueChange = {},
                    label = { Text("Estado*") },
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    readOnly = true,
                    maxLines = 1,
                    singleLine = true
                )
            }

            fun todosValidos(): Boolean{
                if(
                    nome.isNotBlank() &&
                    cpfErro == null &&
                    dataNascimentoErro == null &&
                    usernameErro == null &&
                    senhaErro == null &&
                    cep.isNotBlank() &&
                    listOf(rua, bairro, cidade, estado).all {it.isNotBlank()}
                ) { return true }
                return false
            }

            Button(
                enabled = todosValidos(),
                onClick = {
                    if (todosValidos()) {
                        val endereco = Endereco(cep, rua, numero, bairro, cidade, estado)
                        val dataString = dataNascimento.text
                        val usuario = Usuario("", nome, cpf, dataString, endereco, username, senha, "")

                        val usuarioDAO = UsuarioDAO()
                        usuarioDAO.adicionar(usuario) { sucesso, mensagemErro ->
                            if (sucesso) {
                                Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
                                navController.navigate("login") // Redireciona para a tela de login após o cadastro
                            } else {
                                when (mensagemErro) {
                                    "CPF já cadastrado" -> {
                                        cpfErro = mensagemErro
                                        Toast.makeText(context, mensagemErro, Toast.LENGTH_LONG).show()
                                    }
                                    "Username já cadastrado" -> {
                                        usernameErro = mensagemErro
                                        Toast.makeText(context, mensagemErro, Toast.LENGTH_LONG).show()
                                    }
                                    else -> {
                                        Toast.makeText(context, mensagemErro ?: "Erro ao cadastrar.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().offset(y = (-20).dp)
            ) {
                Text("Cadastrar")
            }

            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth().offset(y = (-20).dp)
            ) { Text("Já tem uma conta? Entre agora.") }

        }
    }
}
