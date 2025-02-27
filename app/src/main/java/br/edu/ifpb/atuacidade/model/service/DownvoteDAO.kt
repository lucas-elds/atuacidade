package br.edu.ifpb.atuacidade.model.service

import br.edu.ifpb.atuacidade.model.Downvote
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

class DownvoteDAO {

    private val db = FirebaseFirestore.getInstance()

    fun buscar(callback: (List<Downvote>) -> Unit) {
        db.collection("downvotes").get()
            .addOnSuccessListener { document ->
                val downvotes = document.toObjects<Downvote>()
                callback(downvotes)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorId(id: String, callback: (Downvote?) -> Unit) {
        db.collection("downvotes").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val downvote = document.toObject<Downvote>()
                    callback(downvote)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun buscarPorUsuarioId(usuarioId: String, callback: (List<Downvote>) -> Unit) {
        db.collection("downvotes").whereEqualTo("usuarioId", usuarioId).get()
            .addOnSuccessListener { document ->
                val downvotes = document.toObjects<Downvote>()
                callback(downvotes)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorPostId(postId: String, callback: (List<Downvote>) -> Unit) {
        db.collection("downvotes").whereEqualTo("postId", postId).get()
            .addOnSuccessListener { document ->
                val downvotes = document.toObjects<Downvote>()
                callback(downvotes)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun adicionar(downvote: Downvote, callback: (Downvote?) -> Unit) {
        db.collection("downvotes").add(downvote)
            .addOnSuccessListener { documentReference ->
                val novoDownvote = downvote.copy(id = documentReference.id)
                callback(novoDownvote)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun deletar(id: String, callback: (Boolean) -> Unit) {
        db.collection("downvotes").document(id).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}