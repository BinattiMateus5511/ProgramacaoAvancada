package jsonlib

import kotlin.reflect.KClass

/**
 * Interface do padrão Visitor para percorrer e operar sobre estruturas JSON.
 * Permite definir comportamentos separados da estrutura dos dados.
 */
interface JsonVisitor {
    fun visitString(value: JSONString)
    fun visitNumber(value: JSONNumber)
    fun visitBoolean(value: JSONBoolean)
    fun visitNull()
    fun visitArray(value: JSONArray)
    fun visitObject(value: JSONObject)
}

/**
 * Validador que garante que um JSONObject não tem chaves duplicadas.
 * Útil para verificar a integridade da estrutura JSON.
 */
class UniqueKeysValidator : JsonVisitor {
    var isValid = true
    private val keysSeen = mutableSetOf<String>()

    /**
     * Verifica se há duplicação de chaves no objeto.
     */
    override fun visitObject(value: JSONObject) {
        keysSeen.clear()
        for (key in value.properties.keys) {
            if (!keysSeen.add(key)) {
                isValid = false
                break
            }
        }
    }

    override fun visitString(value: JSONString) {}
    override fun visitNumber(value: JSONNumber) {}
    override fun visitBoolean(value: JSONBoolean) {}
    override fun visitNull() {}
    override fun visitArray(value: JSONArray) {}
}

/**
 * Validador que garante que todos os elementos de um JSONArray
 * (exceto nulos) são do mesmo tipo.
 */
class HomogeneousArrayValidator : JsonVisitor {
    var isValid = true
    private var expectedClass: KClass<out JSONValue>? = null

    /**
     * Verifica se os elementos têm o mesmo tipo.
     */
    override fun visitArray(value: JSONArray) {
        expectedClass = null
        for (element in value.elements) {
            if (element === JSONNull) continue
            if (expectedClass == null) {
                expectedClass = element::class
            } else if (element::class != expectedClass) {
                isValid = false
                return
            }
        }
    }

    override fun visitString(value: JSONString) {}
    override fun visitNumber(value: JSONNumber) {}
    override fun visitBoolean(value: JSONBoolean) {}
    override fun visitNull() {}
    override fun visitObject(value: JSONObject) {}
}