package no.nav.pensjon.opptjening.adminweb.external.dto

sealed class FilStatusResponse {
    data class ListOk(
        val filer: List<FilMedStatus>
    ) : FilStatusResponse() {
        data class FilMedStatus(
            val filnavn: String,
            val size: Long,
            val lagretMedId: String?,
            val lagresMedId: List<String>
        )
    }

    data class OverforOk(
        val message: String
    ) : FilStatusResponse()

    data class Error(
        val message: String
    ) : FilStatusResponse()
}
