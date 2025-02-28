package br.edu.ifpb.atuacidade.model

import com.google.firebase.firestore.DocumentId
import java.time.LocalDateTime

data class Upvote(
    @DocumentId
    val id: String = "",
    val usuarioId: String = "",
    val postId: String = ""
)
