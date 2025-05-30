package framework

import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

/**
 * Representa o resultado de um match bem-sucedido entre URL e metodo.
 */
data class MatchedRoute(
    val function: KFunction<*>,
    val pathVariables: Map<String, String>
)

/**
 * Faz o match entre o path da URL e os métodos anotados com @Mapping.
 * Retorna o metodo correspondente e as variáveis de path extraídas.
 */
fun matchRoute(controller: Any, url: String): MatchedRoute? {
    val parsed = parseUrl(url)

    val functions = controller::class.memberFunctions

    for (function in functions) {
        val classMapping = controller::class.findAnnotation<Mapping>()?.value ?: ""
        val methodMapping = function.findAnnotation<Mapping>()?.value ?: continue
        val fullMapping = (classMapping.trim('/') + "/" + methodMapping.trim('/')).trim('/')

        val pathTemplate = fullMapping.split("/").filter { it.isNotEmpty() }

        if (pathTemplate.size != parsed.pathSegments.size) continue

        val variables = mutableMapOf<String, String>()
        var matched = true

        for ((templateSegment, actualSegment) in pathTemplate.zip(parsed.pathSegments)) {
            if (templateSegment.startsWith("{") && templateSegment.endsWith("}")) {
                val varName = templateSegment.removePrefix("{").removeSuffix("}")
                variables[varName] = actualSegment
            } else if (templateSegment != actualSegment) {
                matched = false
                break
            }
        }

        if (matched) {
            return MatchedRoute(function, variables)
        }
    }

    return null
}