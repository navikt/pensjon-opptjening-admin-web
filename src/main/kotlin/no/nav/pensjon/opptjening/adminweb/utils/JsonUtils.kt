package no.nav.pensjon.opptjening.adminweb.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

object JsonUtils {
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    fun Any.toJson(): String {
        return objectMapper.writeValueAsString(this)
    }

    fun <T> String.mapToObject(clazz: Class<T>): T {
        return objectMapper.readValue(this, clazz)
    }
}