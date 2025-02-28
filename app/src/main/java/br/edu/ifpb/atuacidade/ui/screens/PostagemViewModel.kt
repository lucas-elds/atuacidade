package br.edu.ifpb.atuacidade.ui.screens

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.atuacidade.model.Localizacao
import br.edu.ifpb.atuacidade.model.Post
import br.edu.ifpb.atuacidade.model.service.PostsDAO
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostagemViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioAuth = SessaoUsuario.usuarioLogado
    private val postsDAO = PostsDAO()

    private val _uiState = MutableStateFlow(PostagemUiState())
    val uiState = _uiState.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    fun atualizarDescricao(descricao: String) {
        _uiState.value = _uiState.value.copy(descricao = descricao)
    }

    fun atualizarURL(url: String) {
        _uiState.value = _uiState.value.copy(url = url)
    }


    fun atualizarCategoria(categoria: String) {
        _uiState.value = _uiState.value.copy(categoriaSelecionada = categoria)
    }

    fun obterLocalizacao(context: Context) {
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    _uiState.value = _uiState.value.copy(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        erro = null
                    )
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        erro = "Não foi possível obter a localização"
                    )
                }
            } catch (e: SecurityException) {
                _uiState.value = _uiState.value.copy(
                    erro = "Permissão de localização necessária"
                )
            }
        }
    }

    fun enviarPostagem() {
        val estado = _uiState.value

        if (!validarCampos(estado)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true)

            val novoPost = criarPost(estado)
            salvarPostagem(novoPost)
        }
    }

    private fun salvarPostagem(post: Post) {
        postsDAO.adicionar(post) { postSalvo ->
            if (postSalvo != null) {
                _uiState.value = _uiState.value.copy(
                    sucesso = true,
                    carregando = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    erro = "Erro ao salvar postagem",
                    carregando = false
                )
            }
        }
    }

    private fun validarCampos(estado: PostagemUiState): Boolean {
        return when {
            estado.descricao.isBlank() -> {
                _uiState.value = estado.copy(erro = "Descrição obrigatória")
                false
            }
            estado.categoriaSelecionada.isBlank() -> {
                _uiState.value = estado.copy(erro = "Selecione uma categoria")
                false
            }
            estado.latitude == null || estado.longitude == null -> {
                _uiState.value = estado.copy(erro = "Obtenha a localização primeiro")
                false
            }
            else -> true
        }
    }

    private fun criarPost(estado: PostagemUiState): Post {
        return Post(
            id = null,
            autorId = usuarioAuth!!.id,
            dataHora = Timestamp.now(),
            midia = estado.url,
            descricao = estado.descricao,
            status = "Pendente",
            categoria = estado.categoriaSelecionada,
            localizacao = Localizacao(
                latitude = estado.latitude!!,
                longitude = estado.longitude!!
            )
        )
    }
}

data class PostagemUiState(
    val descricao: String = "",
    val url: String = "",
    val categoriaSelecionada: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val carregando: Boolean = false,
    val erro: String? = null,
    val sucesso: Boolean = false
)