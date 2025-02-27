package br.edu.ifpb.atuacidade.model.service

import br.edu.ifpb.atuacidade.model.Upvote
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

class UpvoteDAO {

    private val db = FirebaseFirestore.getInstance()

    fun buscar(callback: (List<Upvote>) -> Unit) {
        db.collection("upvotes").get()
            .addOnSuccessListener { document ->
                val upvotes = document.toObjects<Upvote>()
                callback(upvotes)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorId(id: String, callback: (Upvote?) -> Unit) {
        db.collection("upvotes").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val upvote = document.toObject<Upvote>()
                    callback(upvote)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun buscarPorUsuarioId(usuarioId: String, callback: (List<Upvote>) -> Unit) {
        db.collection("upvotes").whereEqualTo("usuarioId", usuarioId).get()
            .addOnSuccessListener { document ->
                val upvotes = document.toObjects<Upvote>()
                callback(upvotes)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorPostId(postId: String, callback: (List<Upvote>) -> Unit) {
        db.collection("upvotes").whereEqualTo("postId", postId).get()
            .addOnSuccessListener { document ->
                val upvotes = document.toObjects<Upvote>()
                callback(upvotes)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun adicionar(upvote: Upvote, callback: (Upvote?) -> Unit) {
        db.collection("upvotes").add(upvote)
            .addOnSuccessListener { documentReference ->
                val novoUpvote = upvote.copy(id = documentReference.id)
                callback(novoUpvote)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun deletar(id: String, callback: (Boolean) -> Unit) {
        db.collection("upvotes").document(id).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}