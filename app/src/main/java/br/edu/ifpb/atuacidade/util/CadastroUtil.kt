package br.edu.ifpb.atuacidade.util

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.util.Base64
import androidx.compose.ui.text.input.TextFieldValue
import br.edu.ifpb.atuacidade.service.UsuarioDAO
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

fun buscarUsername(username: String, callback: (Boolean) -> Unit) {
    val usuarioDAO = UsuarioDAO()
    usuarioDAO.buscarPorUsername(username) { usuario ->
        callback(usuario == null)
    }
}

fun validarSenha(senha: String): Boolean {
    return senha.length >= 8 && senha.any { it.isLetter() } && senha.any { it.isDigit() }
}

fun buscarCep(cep: String, callback: (Map<String, String>) -> Unit) {
    Thread {
        try {
            val resposta = URL("https://viacep.com.br/ws/$cep/json/").readText()
            val json = JSONObject(resposta)

            if (!json.has("erro")) {
                val resultado = mapOf(
                    "rua" to json.getString("logradouro"),
                    "bairro" to json.getString("bairro"),
                    "cidade" to json.getString("localidade"),
                    "estado" to json.getString("uf")
                )
                callback(resultado)
            } else {
                callback(emptyMap())
            }
        } catch (e: Exception) {
            callback(emptyMap())
        }
    }.start()
}
