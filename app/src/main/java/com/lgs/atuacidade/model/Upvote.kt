package com.lgs.atuacidade.model

import java.time.LocalDateTime

data class Upvote(
    val id: String,
    val usuarioId: String,
    val postId: String,
    val data: LocalDateTime
)
