package br.edu.ifpb.atuacidade.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

data class Usuario(
    @DocumentId
    var id: String = "",
    val nome: String = "",
    val cpf: String = "",
    val dataNascimento: String = "",
    val endereco: Endereco? = null,
    val username: String = "",
    val senha: String = "",
    val fotoPerfil: String = ""
) {
    constructor() : this("", "", "", "", null, "", "", "")
}


data class Endereco(
    val cep: String = "",
    val rua: String = "",
    val numero: String = "",
    val bairro: String = "",
    val cidade: String = "",
    val estado: String = ""
) {
    constructor() : this("", "", "", "", "", "")
}
