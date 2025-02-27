package br.edu.ifpb.atuacidade.ui.screens

import android.icu.text.SimpleDateFormat
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
import kotlinx.coroutines.launch
import br.edu.ifpb.atuacidade.ui.util.validarUsername
import br.edu.ifpb.atuacidade.ui.util.validarSenha
import br.edu.ifpb.atuacidade.ui.util.validarCPF
import br.edu.ifpb.atuacidade.ui.util.validarDataNascimento
import br.edu.ifpb.atuacidade.ui.util.buscarCep
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun TelaCadastro(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf(TextFieldValue("")) }
    var genero by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    var cep by remember { mutableStateOf("") }
    var logradouro by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var bairro by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }

    var cpfErro by remember { mutableStateOf<String?>(null) }
    var usernameErro by remember { mutableStateOf<String?>(null) }
    var senhaErro by remember { mutableStateOf<String?>(null) }
    var dataNascimentoErro by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun atualizarCep(cep: String) {
        if (cep.length == 8) {
            coroutineScope.launch {
                val dadosCep = buscarCep(cep)
                logradouro = dadosCep["logradouro"] ?: ""
                bairro = dadosCep["bairro"] ?: ""
                cidade = dadosCep["cidade"] ?: ""
                estado = dadosCep["estado"] ?: ""
            }
        }
    }

    Scaffold(topBar = { CustomAppBar(title = "Cadastro") }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = nome, onValueChange = { nome = it }, label = { Text("Nome") })

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = cpf,
                onValueChange = {
                    cpf = it
                    if(cpf.length >= 11){
                        cpfErro = if (validarCPF(it)) {
                            null
                        } else {
                            "CPF inválido"
                        }
                    }
                },
                label = { Text("CPF") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = cpfErro != null
            )
            cpfErro?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dataNascimento,
                onValueChange = { input ->
                    val digits = input.text.filter { it.isDigit() }
                    val formattedText = when {
                        digits.length <= 2 -> digits
                        digits.length <= 4 -> "${digits.substring(0, 2)}/${digits.substring(2)}"
                        digits.length <= 8 -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4)}"
                        else -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
                    }

                    dataNascimento = TextFieldValue(
                        text = formattedText,
                        selection = TextRange(formattedText.length)
                    )
                },
                label = { Text("Data de nascimento") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = dataNascimentoErro != null
            )
            dataNascimentoErro?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

            // transformar input em um select com as opções "Homem", "Mulher", "Não-binário" e "Outro"
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = genero, onValueChange = { genero = it }, label = { Text("Gênero") })

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                isError = usernameErro != null
            )
            usernameErro?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = senhaErro != null
            )
            senhaErro?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

            Text(text = "Endereço", fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = cep,
                onValueChange = {
                    cep = it
                    atualizarCep(it)
                },
                label = { Text("CEP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = logradouro, onValueChange = {}, label = { Text("Logradouro") }, readOnly = true)
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = numero, onValueChange = { numero = it }, label = { Text("Número") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = bairro, onValueChange = {}, label = { Text("Bairro") }, readOnly = true)
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = cidade, onValueChange = {}, label = { Text("Cidade") }, readOnly = true)
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = estado, onValueChange = {}, label = { Text("Estado") }, readOnly = true)

            Button(onClick = {
                if (cpfErro == null && usernameErro == null && senhaErro == null && dataNascimentoErro == null) {
                    val endereco = Endereco(cep, logradouro, numero, bairro, cidade, estado)
                    val dataString = dataNascimento.text
                    val usuario = Usuario("", nome, cpf, dataString, endereco, genero, username, senha, "")

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
}

@Composable
fun CustomAppBar(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 20.sp, modifier = Modifier.weight(1f))
    }
}
