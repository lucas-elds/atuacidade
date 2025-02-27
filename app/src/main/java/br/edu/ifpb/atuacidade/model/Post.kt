package br.edu.ifpb.atuacidade.model

import com.google.firebase.firestore.DocumentId
import java.time.LocalDateTime
import java.util.Date

data class Post(
    @DocumentId
    val id: String?,
    val autorId: String,
    val dataHora: String,
    val midia: ByteArray,
    val descricao: String,
    val status: String,
    val upvotes: Int,
    val downvotes: Int,
    val categoria: String,
    val localizacao: Localizacao
)

data class Localizacao(
    val latitude: Double,
    val longitude: Double
)