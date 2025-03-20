package br.edu.ifpb.atuacidade.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Post(
    @DocumentId
    var id: String? = null,
    var autorId: String = "",
    var dataHora: Timestamp = Timestamp.now(),
    var midia: String? = null,
    var descricao: String = "",
    var status: String = "",
    var apoios: Int = 0,
    var categoria: String = "",
    var localizacao: Localizacao = Localizacao()
) {
    constructor() : this(null, "", Timestamp.now(), null, "", "", 0, "", Localizacao())
}

data class Localizacao(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) {
    constructor() : this(0.0, 0.0)
}
