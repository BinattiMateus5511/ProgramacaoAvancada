import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JsonModelTest {

    @Test
    fun testJSONString() {
        val json = JSONString("hello \"world\"")
        assertEquals("\"hello \\\"world\\\"\"", json.toJSONString())
    }

    @Test
    fun testJSONNumber() {
        val json = JSONNumber(42)
        assertEquals("42", json.toJSONString())
    }

    @Test
    fun testJSONBoolean() {
        val jsonTrue = JSONBoolean(true)
        val jsonFalse = JSONBoolean(false)
        assertEquals("true", jsonTrue.toJSONString())
        assertEquals("false", jsonFalse.toJSONString())
    }

    @Test
    fun testJSONNull() {
        assertEquals("null", JSONNull.toJSONString())
    }

    @Test
    fun testJSONArraySerialization() {
        val array = JSONArray(
            listOf(
                JSONString("test"),
                JSONNumber(10),
                JSONBoolean(false),
                JSONNull
            )
        )
        val expected = "[\"test\",10,false,null]"
        assertEquals(expected, array.toJSONString())
    }

    @Test
    fun testJSONObjectSerialization() {
        val obj = JSONObject(
            mapOf(
                "name" to JSONString("Mateus"),
                "age" to JSONNumber(30),
                "isDev" to JSONBoolean(true),
                "skills" to JSONArray(listOf(JSONString("Kotlin"), JSONString("QA")))
            )
        )
        val expected = "{\"name\":\"Mateus\",\"age\":30,\"isDev\":true,\"skills\":[\"Kotlin\",\"QA\"]}"
        assertEquals(expected, obj.toJSONString())
    }

    @Test
    fun testJSONArrayMap() {
        val array = JSONArray(listOf(JSONNumber(1), JSONNumber(2), JSONNumber(3)))
        val mapped = array.map { value ->
            if (value is JSONNumber) JSONNumber(value.value.toInt() * 2) else value
        }
        val expectedElements = listOf(JSONNumber(2), JSONNumber(4), JSONNumber(6))
        assertEquals(expectedElements, mapped.elements)
    }

    @Test
    fun testJSONArrayFilter() {
        val array = JSONArray(listOf(JSONNumber(1), JSONNumber(2), JSONNumber(3)))
        val filtered = array.filter { value ->
            value is JSONNumber && value.value.toInt() % 2 != 0
        }
        val expectedElements = listOf(JSONNumber(1), JSONNumber(3))
        assertEquals(expectedElements, filtered.elements)
    }

    @Test
    fun testJSONObjectFilter() {
        val obj = JSONObject(
            mapOf(
                "a" to JSONNumber(1),
                "b" to JSONNumber(2),
                "c" to JSONNumber(3)
            )
        )
        val filtered = obj.filter { it.value is JSONNumber && (it.value as JSONNumber).value.toInt() > 1 }
        val expected = "{\"b\":2,\"c\":3}"
        assertEquals(expected, filtered.toJSONString())
    }
}
