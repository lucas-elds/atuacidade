package com.lgs.atuacidade.model

import java.time.LocalDateTime

data class Post(
    val idPost: String?,
    val autorId: String,
    val dataHora: LocalDateTime,
    val midia: ByteArray,
    val descricao: String,
    val status: String,
    val upvotes: Int,
    val downvotes: Int,
    val tags: List<String>,
    val localizacao: Localizacao
)

data class Localizacao(
    val latitude: Double,
    val longitude: Double
)