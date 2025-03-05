package br.edu.ifpb.atuacidade.service

import br.edu.ifpb.atuacidade.model.Apoio
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

class ApoioDAO {

    private val db = FirebaseFirestore.getInstance()
    private val apoios = db.collection("apoios")

    fun buscar(callback: (List<Apoio>) -> Unit) {
        apoios.get()
            .addOnSuccessListener { document ->
                val apoios = document.toObjects<Apoio>()
                callback(apoios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun usuarioApoiou(postId: String, usuarioId: String, callback: (Apoio?) -> Unit){
        (apoios.whereEqualTo("postId", postId).whereEqualTo("usuarioId", usuarioId)).get()
            .addOnSuccessListener { document ->
                val apoio = document.toObjects<Apoio>().firstOrNull()
                callback(apoio)
            }.addOnFailureListener{
                callback(null)
            }
    }

    fun buscarPorId(id: String, callback: (Apoio?) -> Unit) {
        apoios.document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val apoio = document.toObject<Apoio>()
                    callback(apoio)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun buscarPorUsuarioId(usuarioId: String, callback: (List<Apoio>) -> Unit) {
        apoios.whereEqualTo("usuarioId", usuarioId).get()
            .addOnSuccessListener { document ->
                val apoios = document.toObjects<Apoio>()
                callback(apoios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorPostId(postId: String, callback: (List<Apoio>) -> Unit) {
        apoios.whereEqualTo("postId", postId).get()
            .addOnSuccessListener { document ->
                val apoios = document.toObjects<Apoio>()
                callback(apoios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun adicionar(apoio: Apoio, callback: (Apoio?) -> Unit) {
        apoios.add(apoio)
            .addOnSuccessListener { documentReference ->
                val novoApoio = apoio.copy(id = documentReference.id)
                callback(novoApoio)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun retirar(apoioId: String, callback: (Boolean) -> Unit) {
        apoios.document(apoioId).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}