package jsonlib

import kotlin.reflect.full.memberProperties
import kotlin.reflect.KClass

/**
 * Infere a representação JSON de um objeto Kotlin arbitrário,
 * transformando-o em instâncias do modelo JSON da Fase 1 (JSONString, JSONNumber, JSONObject etc.).
 *
 * Suporta os seguintes tipos:
 * - null
 * - String
 * - Int, Long, Float, Double
 * - Boolean
 * - Enum
 * - List de tipos suportados
 * - Map<String, *> com valores de tipos suportados
 * - Data classes com propriedades de tipos suportados
 *
 * @param value Qualquer valor Kotlin a ser convertido para o modelo JSON
 * @return Um objeto do tipo JSONValue correspondente
 * @throws IllegalArgumentException para tipos não suportados
 */
fun inferJson(value: Any?): JSONValue {
    return when (value) {
        null -> JSONNull
        is String -> JSONString(value)
        is Int, is Long, is Float, is Double -> JSONNumber(value as Number)
        is Boolean -> JSONBoolean(value)
        is Enum<*> -> JSONString(value.name)
        is List<*> -> JSONArray(value.map { inferJson(it) })
        is Map<*, *> -> {
            if (value.keys.all { it is String }) {
                val props = value.mapKeys { it.key as String }
                    .mapValues { inferJson(it.value) }
                JSONObject(props)
            } else {
                throw IllegalArgumentException("Only Map<String, *> is supported")
            }
        }

        else -> {
            val kClass: KClass<out Any> = value::class
            if (kClass.isData) {
                // Faz cast explícito para acessar valores de propriedades refletidas
                val props = kClass.memberProperties.associate { prop ->
                    @Suppress("UNCHECKED_CAST")
                    val propValue = (prop as kotlin.reflect.KProperty1<Any, *>).get(value)
                    prop.name to inferJson(propValue)
                }
                JSONObject(props)
            } else {
                throw IllegalArgumentException("Unsupported type: ${value::class}")
            }
        }
    }}

