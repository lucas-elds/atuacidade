package br.edu.ifpb.atuacidade.data.model

data class AddressDetails(
    val road: String?,
    val house_number: String?,
    val suburb: String?,
    val city: String?,
    val municipality: String?,
    val county: String?,
    val state_district: String?,
    val state: String?,
    val postcode: String?,
    val country: String?,
    val country_code: String?
)

data class GeocodeResponse(
    val place_id: Long,
    val lat: String,
    val lon: String,
    val display_name: String,
    val address: AddressDetails
)