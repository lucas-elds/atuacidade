package br.edu.ifpb.atuacidade.model

import com.google.firebase.firestore.DocumentId

data class Apoio(
    @DocumentId
    val id: String = "",
    val usuarioId: String = "",
    val postId: String = ""
)
