package br.edu.ifpb.atuacidade.model.service

import android.util.Log
import br.edu.ifpb.atuacidade.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

// Classe que pega dados do Firestore
class PostsDAO {

    private val db = FirebaseFirestore.getInstance()

    fun buscar(callback: (List<Post>) -> Unit) {
        db.collection("posts").get()
            .addOnSuccessListener { document ->
                val posts = document.toObjects<Post>()
                callback(posts)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorId(idPost: String, callback: (Post?) -> Unit) {
        db.collection("posts").document(idPost).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val post = document.toObject<Post>()
                    callback(post)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun buscarPorAutorId(autorId: String, callback: (List<Post>) -> Unit) {
        db.collection("posts").whereEqualTo("autorId", autorId).get()
            .addOnSuccessListener { document ->
                val posts = document.toObjects<Post>()
                callback(posts)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun adicionar(post: Post, callback: (Post?) -> Unit) {
        db.collection("posts").add(post)
            .addOnSuccessListener { documentReference ->
                Log.d("PostsDAO", "Postagem salva com ID: ${documentReference.id}")
                val novoPost = post.copy(id = documentReference.id)
                callback(novoPost)
            }
            .addOnFailureListener { e ->
                Log.e("PostsDAO", "Erro ao salvar postagem", e)
                callback(null)
            }
    }

    fun atualizar(post: Post, callback: (Boolean) -> Unit) {
        if (post.id == null) {
            callback(false)
            return
        }

        db.collection("posts").document(post.id!!).set(post)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun deletar(idPost: String, callback: (Boolean) -> Unit) {
        db.collection("posts").document(idPost).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}