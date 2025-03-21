package br.edu.ifpb.atuacidade.util

import br.edu.ifpb.atuacidade.service.ApoioDAO
import br.edu.ifpb.atuacidade.service.PostsDAO
import br.edu.ifpb.atuacidade.ui.composables.screens.SessaoUsuario
import br.edu.ifpb.atuacidade.data.model.Apoio
import br.edu.ifpb.atuacidade.data.model.Comentario
import br.edu.ifpb.atuacidade.data.model.Post
import br.edu.ifpb.atuacidade.service.ComentarioDAO
import br.edu.ifpb.atuacidade.service.UsuarioDAO

val apoioDAO = ApoioDAO()
val usuarioDAO = UsuarioDAO()
val postsDAO = PostsDAO()
val comentarioDAO = ComentarioDAO()

fun apoiar(post : Post, usuarioId : String, callback: (Post?) -> Unit) {
    val novoApoio = Apoio(id = "", postId = post.id!!, usuarioId = usuarioId)
    apoioDAO.adicionar(novoApoio) { apoio ->
        if (apoio != null) {
            post.apoios += 1
            postsDAO.atualizar(post) { atualizado ->
                if(atualizado){
                    callback(post)
                } else {
                    callback(null)
                }
            }
        } else {
            callback(null)
        }
    }
}

fun retirarApoio(post : Post, usuarioId : String, callback: (Post?) -> Unit) {
    apoioDAO.usuarioApoiou(post.id!!, usuarioId) { apoio ->
        if (apoio != null) {
            apoioDAO.retirar(apoio.id) {
                post.apoios -= 1
                postsDAO.atualizar(post) { atualizado ->
                    if (atualizado) {
                        callback(post)
                    } else {
                        callback(null)
                    }
                }
            }
        } else {
            callback(null)
        }
    }
}

fun usuarioApoiou(postId : String, usuarioId: String, callback: (Boolean) -> Unit){
    apoioDAO.usuarioApoiou(postId, usuarioId) { resultado ->
        if(resultado != null){
            callback(true)
        } else {
            callback(false)
        }
    }
}

fun usernameAutor(usuarioId: String, callback: (String) -> Unit){
    usuarioDAO.buscarPorId(usuarioId){ usuario ->
        if(usuario != null){
            callback(usuario.username)
        } else {
            callback("")
        }
    }
}

fun qntApoios(postId : String, callback: (List<Apoio?>) -> Unit){
    apoioDAO.buscarPorPostId(postId){ resultado ->
        callback(resultado )
    }
}

fun comentariosPorPost(postId: String, callback: (List<Comentario>) -> Unit) {
    comentarioDAO.buscarPorPostId(postId) { resultado ->
        callback(resultado)
    }
}

fun comentar(usuarioId: String, postId: String, textoComentario : String, callback: (Comentario) -> Unit){
    val comentario = Comentario(autorId = usuarioId, postId = postId, texto = textoComentario)
    comentarioDAO.adicionar(comentario) { resultado ->
        callback(resultado!!)
    }
}
