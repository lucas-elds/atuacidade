package br.edu.ifpb.atuacidade.service

import br.edu.ifpb.atuacidade.data.model.Comentario
import br.edu.ifpb.atuacidade.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects

class ComentarioDAO {

    private val db = FirebaseFirestore.getInstance()
    private val comentarios = db.collection("comentarios")

    fun adicionar(comentario: Comentario, callback: (Comentario?) -> Unit) {
        comentarios.add(comentario)
            .addOnSuccessListener { documentReference ->
                val novoComentario = comentario.copy(id = documentReference.id)
                callback(novoComentario)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun excluir(comentarioId: String, callback: (Boolean) -> Unit) {
        comentarios.document(comentarioId).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun buscarPorPostId(postId: String, callback: (List<Comentario>) -> Unit) {
        comentarios.whereEqualTo("postId", postId).get()
            .addOnSuccessListener { document ->
                val comentarios = document.toObjects<Comentario>()
                callback(comentarios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}