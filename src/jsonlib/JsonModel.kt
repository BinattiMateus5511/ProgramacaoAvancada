package jsonlib// Modelo base

/**
 * Interface base para qualquer valor JSON.
 * Todos os tipos devem ser serializáveis e aceitarem visitantes.
 */
interface JSONValue {
    fun toJSONString(): String
    fun accept(visitor: JsonVisitor)
}

// Tipos primitivos

/**
 * Representa uma string no modelo JSON, como "texto".
 * @property value o conteúdo da string
 */
data class JSONString(val value: String) : JSONValue {
    override fun toJSONString(): String = "\"${value.replace("\"", "\\\"")}\""
    override fun accept(visitor: JsonVisitor) = visitor.visitString(this)
}

/**
 * Representa um número no modelo JSON, como 10 ou 3.14
 * @property value o número (int, double, etc.)
 */
data class JSONNumber(val value: Number) : JSONValue {
    override fun toJSONString(): String = value.toString()
    override fun accept(visitor: JsonVisitor) = visitor.visitNumber(this)
}

/**
 * Representa um valor booleano (true ou false) no JSON.
 * @property value o valor booleano
 */
data class JSONBoolean(val value: Boolean) : JSONValue {
    override fun toJSONString(): String = value.toString()
    override fun accept(visitor: JsonVisitor) = visitor.visitBoolean(this)
}

/**
 * Representa o valor nulo do JSON.
 */
object JSONNull : JSONValue {
    override fun toJSONString(): String = "null"
    override fun accept(visitor: JsonVisitor) = visitor.visitNull()
}

// JSONArray

/**
 * Representa um array JSON: uma lista ordenada de valores.
 * Suporta operações funcionais de map e filter.
 * @property elements lista de elementos JSON
 */
data class JSONArray(val elements: List<JSONValue>) : JSONValue {
    override fun toJSONString(): String = elements.joinToString(",", "[", "]") { it.toJSONString() }

    override fun accept(visitor: JsonVisitor) {
        visitor.visitArray(this)
        elements.forEach { it.accept(visitor) }
    }

    /**
     * Retorna um novo JSONArray com os elementos transformados por [transform].
     */
    fun map(transform: (JSONValue) -> JSONValue): JSONArray =
        JSONArray(elements.map(transform))

    /**
     * Retorna um novo JSONArray com os elementos que satisfazem [predicate].
     */
    fun filter(predicate: (JSONValue) -> Boolean): JSONArray =
        JSONArray(elements.filter(predicate))
}

// JSONObject

/**
 * Representa um objeto JSON: coleção de pares chave-valor.
 * Suporta operações de filter e visitor.
 * @property properties mapa com as propriedades do objeto
 */
data class JSONObject(val properties: Map<String, JSONValue>) : JSONValue {
    override fun toJSONString(): String = properties.entries.joinToString(",", "{", "}") {
        "\"${it.key}\":${it.value.toJSONString()}"
    }

    override fun accept(visitor: JsonVisitor) {
        visitor.visitObject(this)
        properties.values.forEach { it.accept(visitor) }
    }

    /**
     * Retorna um novo JSONObject com as propriedades filtradas por [predicate].
     */
    fun filter(predicate: (Map.Entry<String, JSONValue>) -> Boolean): JSONObject =
        JSONObject(properties.filter(predicate))
}
