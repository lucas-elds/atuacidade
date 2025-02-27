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
import br.edu.ifpb.atuacidade.ui.util.validarCadastro
import br.edu.ifpb.atuacidade.ui.util.buscarCep
import br.edu.ifpb.atuacidade.ui.util.encodeImageToBase64
import java.util.Locale
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun TelaCadastro(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") }
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

    fun validarCadastro() {
        validarCadastro(cpf, username, senha, dataNascimento) { erros ->
            val (cpfVal, userVal, userDisponivelVal, senhaVal, dataNascimentoVal) = erros
            cpfErro = cpfVal
            usernameErro = userVal ?: userDisponivelVal
            senhaErro = senhaVal
            dataNascimentoErro = dataNascimentoVal
        }
    }

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
                onValueChange = { cpf = it },
                label = { Text("CPF") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = cpfErro != null
            )
            cpfErro?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dataNascimento,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }
                    dataNascimento = when {
                        digits.length <= 2 -> digits
                        digits.length <= 4 -> "${digits.substring(0, 2)}/${digits.substring(2)}"
                        digits.length <= 8 -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4)}"
                        else -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
                    }
                },
                label = { Text("Data de Nascimento (dd/MM/yyyy)") },
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

            // inserir aqui input de foto, que recebe uma imagem e a converte em uma string do tipo base64

            Text(text = "Endereço", fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))

            // os valores dos campos abaixo vao compor um objeto Endereco, que vai ser passado ao criar um novo Usuario
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

            // o botao abaixo deve chamar a funcao adicionar do UsuarioDAO, que passa o objeto de usuario criado, com nome, cpf, data de nascimento, endereco (objeto), genero, username, senha, foto do perfil (nessa ordem)
            Button(onClick = {
                validarCadastro()
                if (cpfErro == null && usernameErro == null && senhaErro == null && dataNascimentoErro == null) {
                    val endereco = Endereco(cep, logradouro, numero, bairro, cidade, estado)
                    val usuario = Usuario("", nome, cpf, dataNascimento, endereco, genero, username, senha, "")

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
