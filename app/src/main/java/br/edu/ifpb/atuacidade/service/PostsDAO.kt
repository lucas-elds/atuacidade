package br.edu.ifpb.atuacidade.service

import android.util.Log
import br.edu.ifpb.atuacidade.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

class PostsDAO {

    private val db = FirebaseFirestore.getInstance()
    private val posts = db.collection("posts")

    fun buscar(callback: (List<Post>) -> Unit) {
        posts.get()
            .addOnSuccessListener { document ->
                val posts = document.toObjects<Post>().sortedByDescending { it.apoios }
                callback(posts)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorId(idPost: String, callback: (Post?) -> Unit) {
        posts.document(idPost).get()
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
        posts.whereEqualTo("autorId", autorId).get()
            .addOnSuccessListener { document ->
                val posts = document.toObjects<Post>()
                callback(posts)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun adicionar(post: Post, callback: (Post?) -> Unit) {
        posts.add(post)
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

        posts.document(post.id!!).set(post)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun deletar(idPost: String, callback: (Boolean) -> Unit) {
        posts.document(idPost).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Para as categorias:
    fun buscarCategorias(callback: (List<String>) -> Unit) {
        db.collection("categorias").get()
            .addOnSuccessListener { documents ->
                val categorias = documents.mapNotNull { it.getString("nome") }
                callback(categorias)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }


}