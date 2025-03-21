package br.edu.ifpb.atuacidade.data.model

data class AddressDetails(
    val road: String? = "Indisponível",
    val house_number: String? = "Indisponível",
    val suburb: String? = "Indisponível",
    val city: String? = "Indisponível",
    val municipality: String? = "Indisponível",
    val county: String? = "Indisponível",
    val state_district: String? = "Indisponível",
    val state: String? = "Indisponível",
    val postcode: String? = "Indisponível",
    val country: String? = "Indisponível",
    val country_code: String? = "Indisponível"
)

data class GeocodeResponse(
    val place_id: Long,
    val lat: String,
    val lon: String,
    val display_name: String,
    val address: AddressDetails
)