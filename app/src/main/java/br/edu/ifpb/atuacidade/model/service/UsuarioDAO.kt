package br.edu.ifpb.atuacidade.model.service

import br.edu.ifpb.atuacidade.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

//classe que pega dados do Firestore
class UsuarioDAO {

    val db = FirebaseFirestore.getInstance()

    fun buscar(callback: (List<Usuario>) -> Unit) {
        db.collection("usuarios").get()
            .addOnSuccessListener { document ->
                val usuarios = document.toObjects<Usuario>()
                callback(usuarios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorNome(nome: String, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").whereEqualTo("nome", nome).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val usuario = document.documents[0].toObject<Usuario>()
                    callback(usuario)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun buscarPorId(id: String, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject<Usuario>()
                    callback(usuario)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun adicionar(usuario: Usuario, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").add(usuario)
            .addOnSuccessListener { documentReference ->
                val novoUsuario = usuario.copy(id = documentReference.id)
                callback(novoUsuario)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun buscarPorUsername(username: String, callback: (Usuario?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios")
            .whereEqualTo("username", username)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                // Obtendo o primeiro documento ou null se não houver
                val usuario = documents.firstOrNull()?.toObject(Usuario::class.java)?.apply {
                    id = documents.first().id // Atribuindo o ID do documento
                }
                callback(usuario) // Retorna o usuário encontrado ou null
            }
            .addOnFailureListener {
                callback(null) // Retorna null em caso de falha
            }
    }




}