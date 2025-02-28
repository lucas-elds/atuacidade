package br.edu.ifpb.atuacidade.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.focus.onFocusChanged
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

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = nome,
            onValueChange = {
                nome = it
            },
            label = { Text("Nome*") },
            shape = RoundedCornerShape(20.dp)
        )

        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            Column (modifier = Modifier.weight(0.5f)) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        if(cpf.length == 11){
                            cpfErro = if (validarCPF(it)) {
                                null
                            } else {
                                "CPF inválido"
                            }
                        }

                        if(cpf.length > 11){
                            cpfErro = "CPF inválido"
                        }

                        if(cpf.isEmpty()){
                            cpfErro = "Informe um CPF"
                        }
                    },
                    label = { Text("CPF*") },
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = cpfErro != null
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = dataNascimentoErro != null
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

        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            Column(modifier = Modifier.weight(0.5f)){
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        if (username.length == 6) {
                            usernameErro = null
                        }
                        if (username.isEmpty()) {
                            usernameErro = "Informe um nome de usuário"
                        }
                    },
                    label = { Text("Nome de usuário*") },
                    shape = RoundedCornerShape(20.dp),
                    isError = usernameErro != null
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
                    isError = senhaErro != null
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

        Text(text = "Endereço", fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))

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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                value = bairro, onValueChange = {},
                label = { Text("Bairro*") },
                shape = RoundedCornerShape(20.dp),
                readOnly = true
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                value = rua, onValueChange = {},
                label = { Text("Rua*") },
                shape = RoundedCornerShape(20.dp),
                readOnly = true
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                value = numero, onValueChange = { numero = it },
                label = { Text("Número") },
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                value = cidade, onValueChange = {},
                label = { Text("Cidade*") },
                shape = RoundedCornerShape(20.dp),
                readOnly = true
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                value = estado, onValueChange = {},
                label = { Text("Estado*") },
                shape = RoundedCornerShape(20.dp),
                readOnly = true
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
                if(todosValidos()){
                    val endereco = Endereco(cep, rua, numero, bairro, cidade, estado)
                    val dataString = dataNascimento.text
                    val usuario = Usuario("", nome, cpf, dataString, endereco, username, senha, "")

                    val usuarioDAO = UsuarioDAO()
                    usuarioDAO.adicionar(usuario) { novoUsuario ->
                        Toast.makeText(context, if (novoUsuario != null) "Cadastro realizado com sucesso!" else "Erro ao cadastrar.", Toast.LENGTH_LONG).show()
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
            Text("Cadastrar")
        }
    }

}