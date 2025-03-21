package br.edu.ifpb.atuacidade.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Comentario (
    @DocumentId
    var id: String? = null,
    var autorId: String = "",
    var postId: String = "",
    var texto: String = "",
    var dataHora: Timestamp = Timestamp.now()
)