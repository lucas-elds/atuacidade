package br.edu.ifpb.atuacidade.model

enum class Categorias(val descricao: String) {
    VAZAMENTO("Vazamento de água ou esgoto"),
    BURACO_NA_RUA("Buraco na rua"),
    ILUMINACAO("Falta de iluminação pública"),
    TERRENO_BALDIO("Terreno baldio com mato alto ou lixo"),
    TRANSPORTE_PUBLICO("Problemas no transporte público"),
    AREAS_DE_RISCO("Áreas de risco (deslizamentos, enchentes)"),
    INFRAESTRUTURA("Problemas de infraestruturas"),
    ATENDIMENTO_SAUDE("Deficiência no atendimento de saúde"),
}
