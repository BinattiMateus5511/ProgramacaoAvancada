// Modelo base
interface JSONValue {
    fun toJSONString(): String
    fun accept(visitor: JsonVisitor)
}

// Tipos primitivos
data class JSONString(val value: String) : JSONValue {
    override fun toJSONString(): String = "\"${value.replace("\"", "\\\"")}\""
    override fun accept(visitor: JsonVisitor) = visitor.visitString(this)
}

data class JSONNumber(val value: Number) : JSONValue {
    override fun toJSONString(): String = value.toString()
    override fun accept(visitor: JsonVisitor) = visitor.visitNumber(this)
}

data class JSONBoolean(val value: Boolean) : JSONValue {
    override fun toJSONString(): String = value.toString()
    override fun accept(visitor: JsonVisitor) = visitor.visitBoolean(this)
}

object JSONNull : JSONValue {
    override fun toJSONString(): String = "null"
    override fun accept(visitor: JsonVisitor) = visitor.visitNull(this)
}

// JSONArray
data class JSONArray(val elements: List<JSONValue>) : JSONValue {
    override fun toJSONString(): String = elements.joinToString(",", "[", "]") { it.toJSONString() }

    override fun accept(visitor: JsonVisitor) {
        visitor.visitArray(this)
        elements.forEach { it.accept(visitor) }
    }

    fun map(transform: (JSONValue) -> JSONValue): JSONArray =
        JSONArray(elements.map(transform))

    fun filter(predicate: (JSONValue) -> Boolean): JSONArray =
        JSONArray(elements.filter(predicate))
}

// JSONObject
data class JSONObject(val properties: Map<String, JSONValue>) : JSONValue {
    override fun toJSONString(): String = properties.entries.joinToString(",", "{", "}") {
        "\"${it.key}\":${it.value.toJSONString()}"
    }

    override fun accept(visitor: JsonVisitor) {
        visitor.visitObject(this)
        properties.values.forEach { it.accept(visitor) }
    }

    fun filter(predicate: (Map.Entry<String, JSONValue>) -> Boolean): JSONObject =
        JSONObject(properties.filter(predicate))
}
