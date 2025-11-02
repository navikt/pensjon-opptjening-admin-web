package no.nav.pensjon.opptjening.adminweb.external.dto

data class ListFilerResponse(
    val filer: List<FilMedStatus>
) {
    data class FilMedStatus(
        val filnavn: String,
        val size: Long,
        val lagretMedId: String?,
        val lagresMedId: List<String>
    )
}
