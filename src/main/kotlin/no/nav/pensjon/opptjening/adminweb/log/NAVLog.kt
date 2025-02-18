package no.nav.pensjon.opptjening.adminweb.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class NAVLog(private val kclass: KClass<*>) {
    val open : Logger = LoggerFactory.getLogger(kclass.java)
    val secure : Logger = LoggerFactory.getLogger("secure")
}