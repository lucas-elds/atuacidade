package com.lgs.atuacidade.model

import java.time.LocalDateTime

data class Downvote(
    val id: String,
    val usuarioId: String,
    val postId: String,
    val data: LocalDateTime
)