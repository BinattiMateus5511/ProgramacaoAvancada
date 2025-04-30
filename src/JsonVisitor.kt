interface JsonVisitor {
    fun visitString(value: JSONString)
    fun visitNumber(value: JSONNumber)
    fun visitBoolean(value: JSONBoolean)
    fun visitNull(value: JSONNull)
    fun visitArray(value: JSONArray)
    fun visitObject(value: JSONObject)
}