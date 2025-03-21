
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.atuacidade.data.model.Localizacao
import br.edu.ifpb.atuacidade.data.model.Post
import br.edu.ifpb.atuacidade.service.PostsDAO
import br.edu.ifpb.atuacidade.ui.composables.screens.SessaoUsuario
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import br.edu.ifpb.atuacidade.util.fetchAddress

class PostagemUtil(application: Application) : AndroidViewModel(application) {

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


    fun carregarCategorias() {
        postsDAO.buscarCategorias { categorias ->
            _uiState.value = _uiState.value.copy(categorias = categorias)
        }
    }

    fun atualizarCategoria(categoria: String) {
        _uiState.value = _uiState.value.copy(categoriaSelecionada = categoria)
    }

    fun obterLocalizacao(context: Context) {
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    fetchAddress(it.latitude, it.longitude) { address ->
                        if (address != null) {
                            _uiState.value = _uiState.value.copy(
                                latitude = it.latitude,
                                longitude = it.longitude,
                                erro = null,
                                dadosEndereco =
                                        "Rua: ${address.road},\n" +
                                        "Número: ${address.house_number},\n" +
                                        "Bairro: ${address.suburb},\n" +
                                        "Cidade: ${address.city}\n" +
                                        "CEP: ${address.postcode}",
                                localizacaoObtida = true
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                erro = "Não foi possível obter a localização"
                            )
                        }
                    }

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

            val urlImagem = estado.imagemSelecionada?.let { uri ->
                uploadImagemParaFirebase(uri)
            } ?: estado.url

            val novoPost = criarPost(estado.copy(url = urlImagem))
            salvarPostagem(novoPost)
        }
    }

    private suspend fun uploadImagemParaFirebase(uri: Uri): String {
        val storageRef = Firebase.storage.reference
        val imagemRef = storageRef.child("${usuarioAuth!!.cpf}/${uri.lastPathSegment}")
        val uploadTask = imagemRef.putFile(uri).await()
        return imagemRef.downloadUrl.await().toString()
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

    fun atualizarImagemSelecionada(uri: Uri) {
        _uiState.value = _uiState.value.copy(imagemSelecionada = uri)
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
    val imagemSelecionada: Uri? = null,
    val categoriaSelecionada: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val carregando: Boolean = false,
    val erro: String? = null,
    val sucesso: Boolean = false,
    val localizacaoObtida: Boolean = false,
    val dadosEndereco: String? = null,
    val categorias: List<String> = emptyList()
)