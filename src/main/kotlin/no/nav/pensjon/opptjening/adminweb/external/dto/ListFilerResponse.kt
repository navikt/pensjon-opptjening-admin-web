package no.nav.pensjon.opptjening.adminweb.external.dto

data class ListFilerResponse(
    val filer: List<FilMedStatus>
) {
    data class FilMedStatus(
        val filnavn: String,
        val size: Long,
        val lagret: Boolean,
    )
}
