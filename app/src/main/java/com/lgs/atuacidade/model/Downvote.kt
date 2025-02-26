package com.lgs.atuacidade.model

import com.google.firebase.firestore.DocumentId
import java.time.LocalDateTime

data class Downvote(
    @DocumentId
    val id: String,
    val usuarioId: String,
    val postId: String,
    val data: LocalDateTime
)