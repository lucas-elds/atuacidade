package br.edu.ifpb.atuacidade.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId


data class Post(
    @DocumentId
    val id: String?,
    val autorId: String,
    val dataHora: Timestamp,
    val midia: String?,
    val descricao: String,
    val status: String,
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val categoria: String,
    val localizacao: Localizacao
)

data class Localizacao(
    val latitude: Double,
    val longitude: Double
)