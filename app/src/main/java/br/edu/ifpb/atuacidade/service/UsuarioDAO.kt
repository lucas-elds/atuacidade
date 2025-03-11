package br.edu.ifpb.atuacidade.service

import br.edu.ifpb.atuacidade.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

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

    fun adicionar(usuario: Usuario, callback: (Boolean, String?) -> Unit) {
        // verifica se o CPF ja ta cadastrado
        buscarPorCpf(usuario.cpf) { usuarioComCpf ->
            if (usuarioComCpf != null) {
                callback(false, "CPF já cadastrado")
            } else {
                // verifica se o username ja ta cadastrado
                buscarPorUsername(usuario.username) { usuarioComUsername ->
                    if (usuarioComUsername != null) {
                        callback(false, "Nome de usuário já cadastrado")
                    } else {
                        db.collection("usuarios").add(usuario)
                            .addOnSuccessListener { documentReference ->
                                val novoUsuario = usuario.copy(id = documentReference.id)
                                callback(true, null)
                            }
                            .addOnFailureListener {
                                callback(false, "Erro ao cadastrar usuário")
                            }
                    }
                }
            }
        }
    }

    fun buscarPorUsername(username: String, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").whereEqualTo("username", username).get()
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

    fun buscarPorCpf(cpf: String, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").whereEqualTo("cpf", cpf).get()
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

    fun deletarUsuario(usuarioId: String, callback: (Boolean) -> Unit) {
        db.collection("usuarios").document(usuarioId).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun atualizarUsuario(usuario: Usuario, callback: (Boolean) -> Unit) {
        if (usuario.id != null) {
            db.collection("usuarios").document(usuario.id).set(usuario)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            callback(false)
        }
    }

}