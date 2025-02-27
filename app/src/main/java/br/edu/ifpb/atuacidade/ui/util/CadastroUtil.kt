package br.edu.ifpb.atuacidade.ui.util

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.util.Base64
import androidx.compose.ui.text.input.TextFieldValue
import br.edu.ifpb.atuacidade.model.service.UsuarioDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.Locale

fun encodeImageToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
}

fun validarCPF(cpf: String): Boolean {
    if (cpf.length != 11 || cpf.all { it == cpf[0] }) return false
    val digitos = cpf.map { it.toString().toInt() }
    for (i in 9..10) {
        val peso = i + 1
        val soma = digitos.subList(0, i).mapIndexed { index, d -> d * (peso - index) }.sum()
        val resto = soma % 11
        if (digitos[i] != if (resto < 2) 0 else 11 - resto) return false
    }
    return true
}

fun validarDataNascimento(dataNascimento: TextFieldValue): Boolean {
    return try {
        val dataString = dataNascimento.text
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false
        dateFormat.parse(dataString)
        true
    } catch (e: Exception) {
        false
    }
}

fun validarUsername(username: String, callback: (String?) -> Unit) {
    if (username.length < 6) {
        callback("Username deve ter pelo menos 6 caracteres")
        return
    }

    val usuarioDAO = UsuarioDAO()
    usuarioDAO.buscarPorUsername(username) { usuario ->
        if (usuario != null) {
            callback("Username já está em uso")
        } else {
            callback(null)
        }
    }
}

fun validarSenha(senha: String): String? {
    return if (senha.length >= 8 && senha.any { it.isLetter() } && senha.any { it.isDigit() }) {
        null
    } else {
        "Senha deve ter pelo menos 8 caracteres alfanuméricos"
    }
}

//fun validarCadastro(cpf: String, username: String, senha: String, dataNascimento: TextFieldValue, callback: (List<String?>) -> Unit) {
//    val cpfErro = if (validarCPF(cpf)) null else "CPF inválido"
//    val usernameErro = if (username.length >= 6) null else "Username deve ter pelo menos 6 caracteres"
//    val senhaErro = if (senha.length >= 8 && senha.any { it.isLetter() } && senha.any { it.isDigit() })
//        null else "Senha deve ter pelo menos 8 caracteres alfanuméricos"
//
//    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//    dateFormat.isLenient = false
//    val dataNascimentoErro = try {
//        val dataString = dataNascimento.text
//        dateFormat.parse(dataString)
//        null
//    } catch (e: Exception) {
//        "Data inválida! Use dd/MM/yyyy."
//    }
//
//    val usuarioDAO = UsuarioDAO()
//
//    usuarioDAO.buscarPorUsername(username) { usuario ->
//        val usernameDisponivelErro = if (usuario == null) null else "Username já está em uso"
//
//        callback(listOf(cpfErro, usernameErro, usernameDisponivelErro, senhaErro, dataNascimentoErro))
//    }
//}

suspend fun buscarCep(cep: String): Map<String, String> {
    return if (cep.length == 8) {
        try {
            val response = withContext(Dispatchers.IO) {
                URL("https://viacep.com.br/ws/$cep/json/").readText()
            }
            val json = JSONObject(response)
            if (!json.has("erro")) {
                mapOf(
                    "logradouro" to json.getString("logradouro"),
                    "bairro" to json.getString("bairro"),
                    "cidade" to json.getString("localidade"),
                    "estado" to json.getString("uf")
                )
            } else emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    } else emptyMap()
}
