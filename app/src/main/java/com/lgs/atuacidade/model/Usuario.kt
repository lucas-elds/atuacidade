package com.lgs.atuacidade.model

import java.time.LocalDate

data class Usuario(
    val idUsuario: String?,
    val nome: String,
    val cpf: String,
    val dataNascimento: LocalDate,
    val endereco: Endereco,
    val genero: String,
    val username: String,
    val senha: String,
    val fotoPerfil: ByteArray
)

data class Endereco(
    val cep: String,
    val rua: String,
    val numero: String,
    val bairro: String,
    val cidade: String,
    val estado: String
)
